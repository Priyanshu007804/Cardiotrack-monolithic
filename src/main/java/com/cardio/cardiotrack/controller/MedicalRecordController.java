package com.cardio.cardiotrack.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cardio.cardiotrack.dto.MedicalRecordRequestDTO;
import com.cardio.cardiotrack.dto.MedicalRecordResponseDTO;
import com.cardio.cardiotrack.service.MedicalRecordService;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients/{patientId}/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(
            MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping    
    public MedicalRecordResponseDTO createMedicalRecord(
            @PathVariable Long patientId,
            @Valid @RequestBody MedicalRecordRequestDTO medicalRecord,
            @AuthenticationPrincipal String email) {

        return medicalRecordService.createMedicalRecord(
                patientId, medicalRecord, email
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR', 'PATIENT')")
    @GetMapping
    public List<MedicalRecordResponseDTO> getRecordsByPatientId(
            @PathVariable Long patientId,
            @AuthenticationPrincipal String email) {

        return medicalRecordService.getRecordsByPatientId(
                patientId, email
        );
    }
}