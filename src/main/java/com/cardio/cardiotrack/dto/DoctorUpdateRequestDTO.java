package com.cardio.cardiotrack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DoctorUpdateRequestDTO(

        @NotBlank(message = "Doctor name is required")
        String fullName,

        @NotBlank(message = "Qualification is required")
        String qualification,

        @NotBlank(message = "Specialization is required")
        String specialization,

        @NotNull(message = "Experience years is required")
        @Min(value = 0, message = "Experience years cannot be negative")
        Integer experienceYears

) {}