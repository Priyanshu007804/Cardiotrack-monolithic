package com.cardio.cardiotrack.dto;

import java.time.LocalDateTime;

public record AssignmentResponseDTO(
        Long id,
        Long doctorId,
        String doctorEmail,
        Long patientId,
        String patientName,
        boolean active,
        LocalDateTime assignedAt
) {
}