package com.cardio.cardiotrack.dto;

public record DoctorResponseDTO(
        Long id,
        String fullName,
        String qualification,
        String specialization,
        Integer experienceYears,
        String registrationNumber
) {}