package com.cardio.cardiotrack.service;

import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cardio.cardiotrack.model.MedicalReport;
import com.cardio.cardiotrack.model.MedicalReportStatus;
import com.cardio.cardiotrack.repository.MedicalReportRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class MedicalReportRetryService {

    private final Cloudinary cloudinary;
    private final MedicalReportRepository medicalReportRepository;
    private final MedicalReportDeletionService medicalReportDeletionService;

    public MedicalReportRetryService(
            Cloudinary cloudinary,
            MedicalReportRepository medicalReportRepository,
            MedicalReportDeletionService medicalReportDeletionService) {

        this.cloudinary = cloudinary;
        this.medicalReportRepository = medicalReportRepository;
        this.medicalReportDeletionService = medicalReportDeletionService;
    }

    // Runs every 5 minutes
    @Scheduled(fixedDelay = 300000)
    public void retryPendingDeletions() {

        List<MedicalReport> pendingReports =
                medicalReportRepository.findByStatus(
                        MedicalReportStatus.PENDING_DELETION);

        for (MedicalReport report : pendingReports) {

            try {
                Map<?, ?> result = cloudinary.uploader().destroy(
                        report.getStorageKey(),
                        ObjectUtils.asMap(
                                "resource_type", "raw",
                                "type", "authenticated"
                        )
                );

                String deletionResult = (String) result.get("result");

                if ("ok".equals(deletionResult)
                        || "not found".equals(deletionResult)) {

                    medicalReportDeletionService.updateStatus(
                            report.getId(),
                            MedicalReportStatus.DELETED
                    );
                }else {
                    System.err.println(
                            "Unexpected Cloudinary deletion result for report ID: "
                            + report.getId() + " | Result: " + deletionResult
                        );
                    }

            } catch (Exception e) {
                // Keep PENDING_DELETION.
                // The next scheduled execution will retry.
                System.err.println(
                        "Retry failed for medical report ID: "
                                + report.getId()
                );
            }
        }
    }
}