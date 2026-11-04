package com.cardio.cardiotrack.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cardio.cardiotrack.dto.MedicalTestRequestDTO;
import com.cardio.cardiotrack.dto.MedicalTestResponseDTO;
import com.cardio.cardiotrack.service.MedicalTestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients/{patientId}/medical-tests")
public class MedicalTestController {

    private final MedicalTestService medicalTestService;

    public MedicalTestController(MedicalTestService medicalTestService) {
        this.medicalTestService = medicalTestService;
    }

    // ==========================================
    // CREATE MEDICAL TEST
    // ==========================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MedicalTestResponseDTO> createMedicalTest(
            @PathVariable Long patientId,
            @RequestParam(required = false) Long hospitalStayId,
            @Valid @RequestBody MedicalTestRequestDTO request,
            @AuthenticationPrincipal String email) {

        MedicalTestResponseDTO response =
                medicalTestService.createMedicalTest(
                        patientId,
                        hospitalStayId,
                        request,
                        email);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==========================================
    // GET MEDICAL TESTS BY PATIENT ID
    // ==========================================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<MedicalTestResponseDTO>> getTestsByPatientId(
            @PathVariable Long patientId,
            @AuthenticationPrincipal String email) {

        List<MedicalTestResponseDTO> tests =
                medicalTestService.getTestsByPatientId(
                        patientId,
                        email);

        return ResponseEntity.ok(tests);
    }
}