package com.cardio.cardiotrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HospitalStayRequestDTO {

    @NotBlank(message = "Admission reason is required")
    @Size(max = 200, message = "Admission reason cannot exceed 200 characters")
    private String admissionReason;

    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;

    public String getAdmissionReason() {
        return admissionReason;
    }

    public void setAdmissionReason(String admissionReason) {
        this.admissionReason = admissionReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}