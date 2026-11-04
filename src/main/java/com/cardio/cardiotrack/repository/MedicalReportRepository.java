package com.cardio.cardiotrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cardio.cardiotrack.model.MedicalReport;
import com.cardio.cardiotrack.model.MedicalReportStatus;

public interface MedicalReportRepository
        extends JpaRepository<MedicalReport, Long> {

    List<MedicalReport> findByPatientId(Long patientId);

    List<MedicalReport> findByMedicalTestId(Long medicalTestId);
    
    List<MedicalReport> findByPatientIdAndStatus(
            Long patientId,
            MedicalReportStatus status
    );

    Optional<MedicalReport> findByIdAndStatus(
            Long id,
            MedicalReportStatus status
    );
    
    List<MedicalReport> findByStatus(MedicalReportStatus status);
}