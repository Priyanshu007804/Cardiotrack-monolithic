package com.cardio.cardiotrack.controller;
import com.cardio.cardiotrack.dto.DoctorRequestDTO;
import com.cardio.cardiotrack.dto.DoctorResponseDTO;
import com.cardio.cardiotrack.dto.DoctorUpdateRequestDTO;
import com.cardio.cardiotrack.service.DoctorService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public DoctorResponseDTO createDoctorProfile(
            @RequestParam Long userId,
            @Valid @RequestBody DoctorRequestDTO request) {

        return doctorService.createDoctorProfile(userId, request);
    }
    
    @GetMapping("/me/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public DoctorResponseDTO getMyDoctorProfile(
            @AuthenticationPrincipal String email) {

        return doctorService.getMyDoctorProfile(email);
    }
    
    @PutMapping("/me/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public DoctorResponseDTO updateMyDoctorProfile(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody DoctorUpdateRequestDTO request) {

        return doctorService.updateMyDoctorProfile(email, request);
    }
}