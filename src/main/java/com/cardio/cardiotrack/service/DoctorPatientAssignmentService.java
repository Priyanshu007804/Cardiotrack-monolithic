package com.cardio.cardiotrack.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cardio.cardiotrack.dto.AssignmentResponseDTO;
import com.cardio.cardiotrack.exception.ResourceNotFoundException;
import com.cardio.cardiotrack.model.Doctor;
import com.cardio.cardiotrack.model.DoctorPatientAssignment;
import com.cardio.cardiotrack.model.Patient;
import com.cardio.cardiotrack.model.Role;
import com.cardio.cardiotrack.model.User;
import com.cardio.cardiotrack.repository.DoctorPatientAssignmentRepository;
import com.cardio.cardiotrack.repository.DoctorRepository;
import com.cardio.cardiotrack.repository.PatientRepository;
import com.cardio.cardiotrack.repository.UserRepository;

@Service
public class DoctorPatientAssignmentService {

    private final DoctorPatientAssignmentRepository assignmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public DoctorPatientAssignmentService(
            DoctorPatientAssignmentRepository assignmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            UserRepository userRepository) {

        this.assignmentRepository = assignmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AssignmentResponseDTO assignDoctorToPatient(
            Long doctorId,
            Long patientId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor profile not found"));

        if (doctor.getUser() == null) {
            throw new IllegalStateException("Doctor profile is not associated with a user account");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found"));

        if (assignmentRepository.existsByDoctor_IdAndPatient_IdAndActiveTrue(
                doctorId, patientId)) {
            throw new IllegalStateException(
                    "Doctor is already assigned to this patient");
        }

        DoctorPatientAssignment assignment = new DoctorPatientAssignment();
        assignment.setDoctor(doctor);
        assignment.setPatient(patient);
        assignment.setActive(true);

        DoctorPatientAssignment savedAssignment = assignmentRepository.save(assignment);

        return mapToDTO(savedAssignment);
    }

    @Transactional(readOnly = true)
    public List<DoctorPatientAssignment> getAssignmentsByDoctor(Long doctorId) {
        return assignmentRepository.findByDoctor_IdAndActiveTrue(doctorId);
    }

    @Transactional(readOnly = true)
    public List<DoctorPatientAssignment> getAssignmentsByPatient(Long patientId) {
        return assignmentRepository.findByPatient_IdAndActiveTrue(patientId);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getMyAssignmentsAsDoctor(String email) {
        User doctorUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (doctorUser.getRole() != Role.DOCTOR) {
            throw new AccessDeniedException(
                    "Only doctors can access their assignments");
        }

        Doctor doctor = doctorRepository.findByUserId(doctorUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor profile not found"));

        return assignmentRepository
                .findByDoctor_IdAndActiveTrue(doctor.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getMyAssignmentsAsPatient(String email) {
        User patientUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (patientUser.getRole() != Role.PATIENT) {
            throw new AccessDeniedException(
                    "Only patients can access their assigned doctors");
        }

        Patient patient = patientRepository.findByUserId(patientUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient profile not found"));

        return assignmentRepository
                .findByPatient_IdAndActiveTrue(patient.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getAllActiveAssignments() {
        return assignmentRepository.findByActiveTrue()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public void deactivateAssignment(Long assignmentId) {
        DoctorPatientAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Assignment not found with ID: " + assignmentId));

        if (!assignment.isActive()) {
            throw new IllegalStateException("Assignment is already inactive");
        }

        assignment.setActive(false);
        assignmentRepository.save(assignment);
    }

    private AssignmentResponseDTO mapToDTO(DoctorPatientAssignment assignment) {
        String doctorEmail = (assignment.getDoctor().getUser() != null)
                ? assignment.getDoctor().getUser().getEmail()
                : null;

        return new AssignmentResponseDTO(
                assignment.getId(),
                assignment.getDoctor().getId(),
                doctorEmail,
                assignment.getPatient().getId(),
                assignment.getPatient().getName(),
                assignment.isActive(),
                assignment.getAssignedAt()
        );
    }
}
