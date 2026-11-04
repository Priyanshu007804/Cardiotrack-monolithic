package com.cardio.cardiotrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cardio.cardiotrack.model.MedicalRecord;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long>{
	List<MedicalRecord> findByPatientId(Long patientId);
}
