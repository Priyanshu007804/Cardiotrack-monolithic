package com.cardio.cardiotrack.service;

import org.springframework.stereotype.Service;

import com.cardio.cardiotrack.repository.DoctorPatientAssignmentRepository;
import com.cardio.cardiotrack.repository.DoctorRepository;
import com.cardio.cardiotrack.repository.MedicalReportRepository;
import com.cardio.cardiotrack.repository.MedicalTestRepository;
import com.cardio.cardiotrack.repository.PatientRepository;
import com.cardio.cardiotrack.repository.UserRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cardio.cardiotrack.dto.MedicalReportDownloadDTO;
import com.cardio.cardiotrack.dto.MedicalReportResponseDTO;
import com.cardio.cardiotrack.exception.ResourceNotFoundException;
import com.cardio.cardiotrack.model.Doctor;
import com.cardio.cardiotrack.model.MedicalReport;
import com.cardio.cardiotrack.model.MedicalReportStatus;
import com.cardio.cardiotrack.model.MedicalTest;
import com.cardio.cardiotrack.model.Patient;
import com.cardio.cardiotrack.model.User;

@Service
public class MedicalReportService {

    private final Cloudinary cloudinary;
    private final MedicalReportRepository medicalReportRepository;
    private final MedicalTestRepository medicalTestRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );
    private final DoctorRepository doctorRepository;
    private final DoctorPatientAssignmentRepository doctorPatientAssignmentRepository;
    private final MedicalReportDeletionService medicalReportDeletionService;
    
    
    
    public MedicalReportService(
            Cloudinary cloudinary,
            MedicalReportRepository medicalReportRepository,
            MedicalTestRepository medicalTestRepository,
            PatientRepository patientRepository,
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            DoctorPatientAssignmentRepository doctorPatientAssignmentRepository,
            MedicalReportDeletionService medicalReportDeletionService) {

        this.cloudinary = cloudinary;
        this.medicalReportRepository = medicalReportRepository;
        this.medicalTestRepository = medicalTestRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.doctorRepository=doctorRepository;
        this.doctorPatientAssignmentRepository=doctorPatientAssignmentRepository;
        this.medicalReportDeletionService = medicalReportDeletionService;
    }
    
    
    
    private void authorizeReportUpload(User user, Long patientId) {

        String role = user.getRole().name();

        // Admin and Staff can upload reports
        if (role.equals("ADMIN") || role.equals("STAFF")) {
            return;
        }

        // Doctors can upload reports only for assigned patients
        if (role.equals("DOCTOR")) {

            Doctor doctor = doctorRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Doctor profile not found"));

            boolean assigned = doctorPatientAssignmentRepository
                    .existsByDoctor_IdAndPatient_IdAndActiveTrue(
                            doctor.getId(), patientId);

            if (!assigned) {
                throw new AccessDeniedException(
                        "You can only upload reports for your assigned patients");
            }

            return;
        }

        // Patients cannot upload medical reports
        throw new AccessDeniedException(
                "You are not authorized to upload medical reports");
    }
    
    
    private boolean hasValidFileSignature(byte[] fileBytes, String contentType) {
        if (fileBytes == null || fileBytes.length < 4) {
            return false;
        }

        // PDF: %PDF
        if ("application/pdf".equals(contentType)) {
            return fileBytes.length >= 5
                    && fileBytes[0] == '%'
                    && fileBytes[1] == 'P'
                    && fileBytes[2] == 'D'
                    && fileBytes[3] == 'F'
                    && fileBytes[4] == '-';
        }

        // JPEG: FF D8 FF
        if ("image/jpeg".equals(contentType)) {
            return fileBytes.length >= 3
                    && (fileBytes[0] & 0xFF) == 0xFF
                    && (fileBytes[1] & 0xFF) == 0xD8
                    && (fileBytes[2] & 0xFF) == 0xFF;
        }

        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if ("image/png".equals(contentType)) {
            byte[] signature = {
                    (byte) 0x89, 0x50, 0x4E, 0x47,
                    0x0D, 0x0A, 0x1A, 0x0A
            };

            if (fileBytes.length < signature.length) {
                return false;
            }

            for (int i = 0; i < signature.length; i++) {
                if (fileBytes[i] != signature[i]) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
    
    @Transactional
    public MedicalReportResponseDTO uploadMedicalReport(
            Long patientId,
            Long medicalTestId,
            MultipartFile file,
            String email) throws IOException {

        // 1. Validate the uploaded file
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "File size cannot exceed 10 MB");
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Only PDF, JPEG, and PNG files are allowed");
        }
        
        
        byte[] fileBytes = file.getBytes();

        if (!hasValidFileSignature(fileBytes, contentType)) {
            throw new IllegalArgumentException(
                    "File content does not match the allowed file type");
        }
        
        // 2. Find the authenticated user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found"));

        // 3. Check upload permissions
        authorizeReportUpload(user, patientId);

        // 4. Verify the patient exists
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + patientId));

        // 5. Validate the optional medical test
        MedicalTest medicalTest = null;

        if (medicalTestId != null) {
            medicalTest = medicalTestRepository.findById(medicalTestId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Medical test not found with id: " + medicalTestId));

            if (!medicalTest.getPatient().getId().equals(patientId)) {
                throw new IllegalArgumentException(
                        "Medical test does not belong to this patient");
            }
        }

        // 6. Generate a unique storage identifier
        String storageKey = UUID.randomUUID().toString();

        // 7. Upload the file to Cloudinary
        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                fileBytes,
                Map.of(
                        "public_id", storageKey,
                        "resource_type", "raw",
                        "type", "authenticated"
                )
        );

        // 8. Save the report metadata
        MedicalReport report = new MedicalReport();
        report.setPatient(patient);
        report.setMedicalTest(medicalTest);
        report.setUploadedBy(user);
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            originalFileName = "medical-report";
        }

        originalFileName = java.nio.file.Paths
                .get(originalFileName)
                .getFileName()
                .toString();

        report.setFileName(originalFileName);
        report.setContentType(contentType);
        report.setStorageKey((String) uploadResult.get("public_id"));
        report.setFileSizeBytes(file.getSize());
        report.setUploadedAt(LocalDateTime.now());

        MedicalReport savedReport;

        try {
            savedReport = medicalReportRepository.save(report);
            medicalReportRepository.flush();
        } catch (Exception e) {
            try {
                cloudinary.uploader().destroy(
                        storageKey,
                        ObjectUtils.asMap(
                                "resource_type", "raw",
                                "type", "authenticated"
                        )
                );
            } catch (Exception cleanupException) {
                e.addSuppressed(cleanupException);
            }

            throw e;
        }
        
        // 9. Return safe metadata to the client
        return new MedicalReportResponseDTO(
                savedReport.getId(),
                savedReport.getPatient().getId(),
                savedReport.getMedicalTest() != null
                        ? savedReport.getMedicalTest().getId() : null,
                savedReport.getFileName(),
                savedReport.getContentType(),
                savedReport.getFileSizeBytes(),
                savedReport.getUploadedAt(),
                savedReport.getUploadedBy().getId()
        );
    }
    
    
    private void authorizeReportAccess(User user, Long patientId) {

        String role = user.getRole().name();

        // Admin and Staff can access patient reports
        if (role.equals("ADMIN") || role.equals("STAFF")) {
            return;
        }

        // Patients can access only their own reports
        if (role.equals("PATIENT")) {

            Patient patient = patientRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Patient profile not found"));

            if (!patient.getId().equals(patientId)) {
                throw new AccessDeniedException(
                        "You can only access your own medical reports");
            }

            return;
        }

        // Doctors can access reports of their assigned patients
        if (role.equals("DOCTOR")) {

            Doctor doctor = doctorRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Doctor profile not found"));

            boolean assigned = doctorPatientAssignmentRepository
                    .existsByDoctor_IdAndPatient_IdAndActiveTrue(
                            doctor.getId(), patientId);

            if (!assigned) {
                throw new AccessDeniedException(
                        "You can only access reports of your assigned patients");
            }

            return;
        }

        throw new AccessDeniedException(
                "You are not authorized to access medical reports");
    }
    
    
    @Transactional(readOnly = true)
    public List<MedicalReportResponseDTO> getReportsByPatientId(
            Long patientId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found"));

        authorizeReportAccess(user, patientId);

        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException(
                    "Patient not found with id: " + patientId);
        }

        return medicalReportRepository
                .findByPatientIdAndStatus(
                        patientId,
                        MedicalReportStatus.ACTIVE
                )
                .stream()
                .map(report -> new MedicalReportResponseDTO(
                        report.getId(),
                        report.getPatient().getId(),
                        report.getMedicalTest() != null
                                ? report.getMedicalTest().getId() : null,
                        report.getFileName(),
                        report.getContentType(),
                        report.getFileSizeBytes(),
                        report.getUploadedAt(),
                        report.getUploadedBy().getId()
                ))
                .toList();
    }
    
    
    
    
    @Transactional(readOnly = true)
    public MedicalReportDownloadDTO getMedicalReportForDownload(
          Long patientId,  Long reportId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"));

        MedicalReport report = medicalReportRepository
                .findByIdAndStatus(
                        reportId,
                        MedicalReportStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Medical report not found"));

        // Verify that the user is allowed to access this patient's reports.
        authorizeReportAccess(user, report.getPatient().getId());
        
        if (!report.getPatient().getId().equals(patientId)) {
            throw new ResourceNotFoundException("Medical report not found");
        }
       

            byte[] fileData;

            try {
                Map<?, ?> downloadResult = cloudinary.api()
                        .resource(
                                report.getStorageKey(),
                                ObjectUtils.asMap(
                                        "resource_type", "raw",
                                        "type", "authenticated"
                                )
                        );

                String secureUrl = (String) downloadResult.get("secure_url");

                try (java.io.InputStream inputStream =
                             new java.net.URI(secureUrl).toURL().openStream()) {
                    fileData = inputStream.readAllBytes();
                }

            } catch (Exception e) {
                throw new IllegalStateException(
                        "Failed to retrieve medical report from Cloudinary", e);
            }
            return new MedicalReportDownloadDTO(
                    report.getFileName(),
                    report.getContentType(),
                    fileData
            );
    
        }
    
    
    @Transactional
    public void deleteMedicalReport(Long patientId, Long reportId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found"));

        MedicalReport report = medicalReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medical report not found"));

        if (!report.getPatient().getId().equals(patientId)) {
            throw new ResourceNotFoundException("Medical report not found");
        }

        authorizeReportAccess(user, patientId);

        String role = user.getRole().name();

        if (!role.equals("ADMIN") && !role.equals("STAFF")) {
            throw new AccessDeniedException(
                    "Only ADMIN and STAFF can delete medical reports");
        }

        if (report.getStatus() == MedicalReportStatus.DELETED) {
            throw new ResourceNotFoundException("Medical report not found");
        }

        // Step 1: Mark deletion as pending.
        medicalReportDeletionService.updateStatus(
                reportId,
                MedicalReportStatus.PENDING_DELETION
        );

        // Step 2: Attempt Cloudinary deletion.
        try {
            Map<?, ?> result = cloudinary.uploader().destroy(
                    report.getStorageKey(),
                    ObjectUtils.asMap(
                            "resource_type", "raw",
                            "type", "authenticated"
                    )
            );

            String deletionResult = (String) result.get("result");

            if (!"ok".equals(deletionResult)
                    && !"not found".equals(deletionResult)) {
                throw new IllegalStateException(
                        "Cloudinary could not delete the medical report");
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Medical report deletion is pending and can be retried", e);
        }

        // Step 3: Mark deletion as completed.
        medicalReportDeletionService.updateStatus(
                reportId,
                MedicalReportStatus.DELETED
        );
    }
    
    
}
