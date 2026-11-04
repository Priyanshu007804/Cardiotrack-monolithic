package com.cardio.cardiotrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cardio.cardiotrack.model.DoctorPatientAssignment;

public interface DoctorPatientAssignmentRepository
        extends JpaRepository<DoctorPatientAssignment, Long> {

    boolean existsByDoctor_IdAndPatient_IdAndActiveTrue(
            Long doctorId,
            Long patientId);

    List<DoctorPatientAssignment> findByDoctor_IdAndActiveTrue(
            Long doctorId);

    List<DoctorPatientAssignment> findByPatient_IdAndActiveTrue(
            Long patientId);
    List<DoctorPatientAssignment> findByActiveTrue();
}