package com.cardio.cardiotrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cardio.cardiotrack.model.MedicalTest;

public interface MedicalTestRepository
        extends JpaRepository<MedicalTest, Long> {

    List<MedicalTest> findByPatientId(Long patientId);

    List<MedicalTest> findByHospitalStayId(Long hospitalStayId);
}