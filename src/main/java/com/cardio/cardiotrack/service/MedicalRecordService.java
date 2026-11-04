package com.cardio.cardiotrack.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cardio.cardiotrack.model.Doctor;
import com.cardio.cardiotrack.model.MedicalRecord;
import com.cardio.cardiotrack.model.Patient;
import com.cardio.cardiotrack.model.Role;
import com.cardio.cardiotrack.model.User;
import com.cardio.cardiotrack.repository.DoctorPatientAssignmentRepository;
import com.cardio.cardiotrack.repository.DoctorRepository;
import com.cardio.cardiotrack.repository.MedicalRecordRepository;
import com.cardio.cardiotrack.repository.PatientRepository;
import com.cardio.cardiotrack.repository.UserRepository;
import com.cardio.cardiotrack.dto.MedicalRecordRequestDTO;
import com.cardio.cardiotrack.dto.MedicalRecordResponseDTO;
import com.cardio.cardiotrack.exception.ResourceNotFoundException;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorPatientAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    public MedicalRecordService(
            MedicalRecordRepository medicalRecordRepository,
            PatientRepository patientRepository,
            DoctorPatientAssignmentRepository assignmentRepository,
            UserRepository userRepository,
            DoctorRepository doctorRepository) {

        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.doctorRepository=doctorRepository;
    }
    
    private User getAuthenticatedUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"));
    }
    
    private void authorizePatientAccess(User user, Long patientId) {

        if (user.getRole() == Role.ADMIN || user.getRole() == Role.STAFF) {
            return;
        }

        if (user.getRole() == Role.PATIENT) {

            Patient patient = patientRepository.findByUserId(user.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Patient profile not found"));

            if (!patient.getId().equals(patientId)) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "You can only access your own medical records");
            }

            return;
        }

        if (user.getRole() == Role.DOCTOR) {

            Doctor doctor = doctorRepository.findByUserId(user.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Doctor profile not found"));

            boolean assigned = assignmentRepository
                    .existsByDoctor_IdAndPatient_IdAndActiveTrue(
                            doctor.getId(), patientId);

            if (!assigned) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "You are not assigned to this patient");
            }

            return;
        }
        
        throw new org.springframework.security.access.AccessDeniedException(
                "You are not authorized to access medical records");
    }
    
    
    
    private void authorizeRecordCreation(User user, Long patientId) {

        if (user.getRole() == Role.ADMIN || user.getRole() == Role.STAFF) {
            return;
        }

        if (user.getRole() == Role.DOCTOR) {

            Doctor doctor = getDoctorProfile(user);

            boolean assigned = assignmentRepository
                    .existsByDoctor_IdAndPatient_IdAndActiveTrue(
                            doctor.getId(), patientId);

            if (!assigned) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "You can only create records for patients assigned to you");
            }

            return;
        }

        throw new org.springframework.security.access.AccessDeniedException(
                "You are not authorized to create medical records");
    }
    
    
    
    
    private Doctor getDoctorProfile(User user) {
        return doctorRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor profile not found"));
    }
    private MedicalRecordResponseDTO toResponseDTO(MedicalRecord record) {
        return new MedicalRecordResponseDTO(
            record.getId(),
            record.getDiagnosis(),
            record.getNotes(),
            record.getRecordedAt(),
            record.getPatient().getId()
        );
    }
    
    

    public MedicalRecordResponseDTO createMedicalRecord(
            Long patientId,
            MedicalRecordRequestDTO request,
            String email) {
    	
    	User user = getAuthenticatedUser(email);
    	authorizeRecordCreation(user, patientId);
    	
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Patient not found with id: " + patientId
                )
            );

        MedicalRecord record = new MedicalRecord();
        record.setDiagnosis(request.getDiagnosis());
        record.setNotes(request.getNotes());
        record.setPatient(patient);
        record.setRecordedAt(LocalDateTime.now());

        MedicalRecord savedRecord = medicalRecordRepository.save(record);

        return toResponseDTO(savedRecord);
    }

    public List<MedicalRecordResponseDTO> getRecordsByPatientId(
            Long patientId, String email) {
    	
    	User user = getAuthenticatedUser(email);
    	authorizePatientAccess(user, patientId);
    	
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException(
                "Patient not found with id: " + patientId
            );
        }

        return medicalRecordRepository.findByPatientId(patientId).stream().map(this::toResponseDTO).toList();
    }
}