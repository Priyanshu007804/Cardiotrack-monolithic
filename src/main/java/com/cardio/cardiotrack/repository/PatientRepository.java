package com.cardio.cardiotrack.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cardio.cardiotrack.model.Patient;
public interface PatientRepository extends JpaRepository<Patient,Long>{
	Optional<Patient> findByUserId(Long userId);
}
