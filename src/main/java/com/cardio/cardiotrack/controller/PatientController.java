package com.cardio.cardiotrack.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cardio.cardiotrack.dto.PatientRequestDTO;
import com.cardio.cardiotrack.dto.PatientResponseDTO;
import com.cardio.cardiotrack.dto.PatientUpdateRequestDTO;
import com.cardio.cardiotrack.model.Patient;
import com.cardio.cardiotrack.service.PatientService;

import jakarta.validation.Valid;

@RestController
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    @PostMapping("/api/patients")
    public PatientResponseDTO createPatient(@Valid @RequestBody PatientRequestDTO request) {
    	return patientService.createPatient(request);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','DOCTOR')")
    @GetMapping("/api/patients")
   public List<PatientResponseDTO> getAllPatients(){
    	return patientService.getAllPatients();
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','DOCTOR')")
    @GetMapping("/api/patients/{id}")
    public PatientResponseDTO getPatientbyId(@PathVariable Long id)
    {
    	return patientService.getPatientById(id);
    	
    }
    
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/api/patients/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','DOCTOR')")
    @PutMapping("/api/patients/{id}")
    public PatientResponseDTO updatePatient(
            @PathVariable Long id,
            @RequestBody PatientUpdateRequestDTO patient) {

        return patientService.updatePatient(id, patient);
    }
    
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/api/patients/me/profile")
    public ResponseEntity<PatientResponseDTO> createMyProfile(
            @Valid @RequestBody PatientRequestDTO request,
            Authentication authentication) {

        String email = authentication.getName();

        Patient patient = patientService.createPatientProfileByEmail(
                email, request);

        PatientResponseDTO response = new PatientResponseDTO(
                patient.getId(),
                patient.getName(),
                patient.getAge(),
                patient.getGender()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/api/patients/me/profile")
    public PatientResponseDTO getMyProfile(Authentication authentication) {

        String email = authentication.getName();

        return patientService.getMyPatientProfile(email);
    }
    
    
    @PreAuthorize("hasRole('PATIENT')")
    @PutMapping("/api/patients/me/profile")
    public PatientResponseDTO updateMyProfile(
            @Valid @RequestBody PatientUpdateRequestDTO request,
            Authentication authentication) {

        String email = authentication.getName();

        return patientService.updateMyPatientProfile(email, request);
    }
}