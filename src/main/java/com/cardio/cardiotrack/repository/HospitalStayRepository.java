package com.cardio.cardiotrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cardio.cardiotrack.model.HospitalStay;

public interface HospitalStayRepository extends JpaRepository<HospitalStay, Long>{
	List<HospitalStay> findByPatientId(Long patientId);
}
