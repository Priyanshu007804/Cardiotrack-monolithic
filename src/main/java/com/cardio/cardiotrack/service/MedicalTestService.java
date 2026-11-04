package com.cardio.cardiotrack.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cardio.cardiotrack.dto.MedicalTestRequestDTO;
import com.cardio.cardiotrack.dto.MedicalTestResponseDTO;
import com.cardio.cardiotrack.exception.ResourceNotFoundException;
import com.cardio.cardiotrack.model.Doctor;
import com.cardio.cardiotrack.model.HospitalStay;
import com.cardio.cardiotrack.model.MedicalTest;
import com.cardio.cardiotrack.model.Patient;
import com.cardio.cardiotrack.model.User;
import com.cardio.cardiotrack.repository.DoctorPatientAssignmentRepository;
import com.cardio.cardiotrack.repository.DoctorRepository;
import com.cardio.cardiotrack.repository.HospitalStayRepository;
import com.cardio.cardiotrack.repository.MedicalTestRepository;
import com.cardio.cardiotrack.repository.PatientRepository;
import com.cardio.cardiotrack.repository.UserRepository;

@Service
public class MedicalTestService {

    private final MedicalTestRepository medicalTestRepository;
    private final PatientRepository patientRepository;
    private final HospitalStayRepository hospitalStayRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorPatientAssignmentRepository doctorPatientAssignmentRepository;

    public MedicalTestService(
            MedicalTestRepository medicalTestRepository,
            PatientRepository patientRepository,
            HospitalStayRepository hospitalStayRepository,
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            DoctorPatientAssignmentRepository doctorPatientAssignmentRepository) {

        this.medicalTestRepository = medicalTestRepository;
        this.patientRepository = patientRepository;
        this.hospitalStayRepository = hospitalStayRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.doctorPatientAssignmentRepository = doctorPatientAssignmentRepository;
    }

    // CREATE MEDICAL TEST

    @Transactional
    public MedicalTestResponseDTO createMedicalTest(
            Long patientId,
            Long hospitalStayId,
            MedicalTestRequestDTO request,
            String email) {

        // 1. Identify authenticated user
        User user = getAuthenticatedUser(email);

        // 2. Authorize test creation
        authorizeTestCreation(user, patientId);

        // 3. Verify patient exists
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with id: " + patientId));

        // 4. Validate optional hospital stay
        HospitalStay hospitalStay = null;

        if (hospitalStayId != null) {

            hospitalStay = hospitalStayRepository.findById(hospitalStayId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Hospital stay not found with id: "
                                            + hospitalStayId));

            // Ensure the hospital stay belongs to the requested patient
            if (!hospitalStay.getPatient().getId().equals(patientId)) {
                throw new IllegalArgumentException(
                        "Hospital stay does not belong to this patient");
            }
        }

        // 5. Create medical test entity
        MedicalTest medicalTest = new MedicalTest();

        medicalTest.setPatient(patient);
        medicalTest.setHospitalStay(hospitalStay);
        medicalTest.setTestName(request.getTestName());
        medicalTest.setTestType(request.getTestType());
        medicalTest.setTestDate(request.getTestDate());
        medicalTest.setResults(request.getResults());
        medicalTest.setInterpretation(request.getInterpretation());
        medicalTest.setCreatedAt(LocalDateTime.now());

        // 6. Save entity
        MedicalTest savedTest = medicalTestRepository.save(medicalTest);

        // 7. Return response DTO
        return toResponseDTO(savedTest);
    }

    // GET MEDICAL TESTS BY PATIENT ID

    @Transactional(readOnly = true)
    public List<MedicalTestResponseDTO> getTestsByPatientId(
            Long patientId,
            String email) {

        // 1. Identify authenticated user
        User user = getAuthenticatedUser(email);

        // 2. Authorize access to patient records
        authorizeTestAccess(user, patientId);

        // 3. Verify patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException(
                    "Patient not found with id: " + patientId);
        }

        // 4. Fetch and map medical tests
        return medicalTestRepository.findByPatientId(patientId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // GET AUTHENTICATED USER

    private User getAuthenticatedUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"));
    }

    // GET DOCTOR PROFILE

    private Doctor getDoctorProfile(User user) {

        return doctorRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor profile not found"));
    }

    // AUTHORIZE MEDICAL TEST CREATION

    private void authorizeTestCreation(User user, Long patientId) {

        String role = user.getRole().name();

        // ADMIN and STAFF can create medical tests
        if (role.equals("ADMIN") || role.equals("STAFF")) {
            return;
        }

        if (role.equals("DOCTOR")) {

            Doctor doctor = getDoctorProfile(user);

            boolean assigned = doctorPatientAssignmentRepository
                    .existsByDoctor_IdAndPatient_IdAndActiveTrue(
                            doctor.getId(),
                            patientId);

            if (!assigned) {
                throw new AccessDeniedException(
                        "You can only create medical tests "
                                + "for your assigned patients");
            }

            return;
        }

        // PATIENT and other roles cannot create medical tests
        throw new AccessDeniedException(
                "You are not authorized to create medical tests");
    }

    // AUTHORIZE MEDICAL TEST ACCESS

    private void authorizeTestAccess(User user, Long patientId) {

        String role = user.getRole().name();

        // ADMIN and STAFF can view all patients' tests
        if (role.equals("ADMIN") || role.equals("STAFF")) {
            return;
        }

        // PATIENT can view only their own tests
        if (role.equals("PATIENT")) {

            Patient patient = patientRepository
                    .findByUserId(user.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Patient profile not found"));

            if (!patient.getId().equals(patientId)) {
                throw new AccessDeniedException(
                        "You can only access your own medical tests");
            }

            return;
        }

        // DOCTOR can view tests only for actively assigned patients
        if (role.equals("DOCTOR")) {

            Doctor doctor = getDoctorProfile(user);

            boolean assigned = doctorPatientAssignmentRepository
                    .existsByDoctor_IdAndPatient_IdAndActiveTrue(
                            doctor.getId(),
                            patientId);

            if (!assigned) {
                throw new AccessDeniedException(
                        "You can only access medical tests "
                                + "of your assigned patients");
            }

            return;
        }

        // Deny access to any other role
        throw new AccessDeniedException(
                "You are not authorized to access medical tests");
    }

    // ENTITY TO RESPONSE DTO MAPPING

    private MedicalTestResponseDTO toResponseDTO(MedicalTest test) {

        Long hospitalStayId = test.getHospitalStay() != null
                ? test.getHospitalStay().getId()
                : null;

        return new MedicalTestResponseDTO(
                test.getId(),
                test.getPatient().getId(),
                hospitalStayId,
                test.getTestName(),
                test.getTestType(),
                test.getTestDate(),
                test.getResults(),
                test.getInterpretation(),
                test.getCreatedAt());
    }
}