package com.cardio.cardiotrack.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cardio.cardiotrack.model.MedicalReport;
import com.cardio.cardiotrack.model.MedicalReportStatus;
import com.cardio.cardiotrack.repository.MedicalReportRepository;

@Service
public class MedicalReportDeletionService {

    private final MedicalReportRepository medicalReportRepository;

    public MedicalReportDeletionService(
            MedicalReportRepository medicalReportRepository) {
        this.medicalReportRepository = medicalReportRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(Long reportId, MedicalReportStatus status) {

        MedicalReport report = medicalReportRepository.findById(reportId)
                .orElseThrow(() ->
                        new IllegalStateException("Medical report not found"));

        report.setStatus(status);

        medicalReportRepository.saveAndFlush(report);
    }
}