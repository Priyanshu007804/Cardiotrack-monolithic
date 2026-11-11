package com.cardio.cardiotrack.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.cardio.cardiotrack.dto.MedicalReportDownloadDTO;
import com.cardio.cardiotrack.dto.MedicalReportResponseDTO;
import com.cardio.cardiotrack.service.MedicalReportService;

@RestController
@RequestMapping("/api/patients/{patientId}/medical-reports")
public class MedicalReportController {

    private final MedicalReportService medicalReportService;

    public MedicalReportController(
            MedicalReportService medicalReportService) {
        this.medicalReportService = medicalReportService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    public ResponseEntity<MedicalReportResponseDTO> uploadMedicalReport(
            @PathVariable Long patientId,
            @RequestParam(required = false) Long medicalTestId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal String email) throws IOException {

        MedicalReportResponseDTO response =
                medicalReportService.uploadMedicalReport(
                        patientId, medicalTestId, file, email);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<MedicalReportResponseDTO>> getReportsByPatientId(
            @PathVariable Long patientId,
            @AuthenticationPrincipal String email) {

        List<MedicalReportResponseDTO> reports =
                medicalReportService.getReportsByPatientId(patientId, email);

        return ResponseEntity.ok(reports);
    }
    
    
    @GetMapping("/{reportId}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ByteArrayResource> downloadMedicalReport(
            @PathVariable Long patientId,
            @PathVariable Long reportId,
            @AuthenticationPrincipal String email) {

        MedicalReportDownloadDTO report =
                medicalReportService.getMedicalReportForDownload(
                        patientId, reportId, email);

        ByteArrayResource resource =
                new ByteArrayResource(report.getFileData());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(report.getContentType()))
                .header(
                        "Content-Disposition",
                        "attachment; filename=\"" + report.getFileName() + "\""
                )
                .contentLength(report.getFileData().length)
                .body(resource);
    }
    
    @DeleteMapping("/{reportId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> deleteMedicalReport(
            @PathVariable Long patientId,
            @PathVariable Long reportId,
            @AuthenticationPrincipal String email) {

        medicalReportService.deleteMedicalReport(patientId, reportId, email);

        return ResponseEntity.noContent().build();
    }
}