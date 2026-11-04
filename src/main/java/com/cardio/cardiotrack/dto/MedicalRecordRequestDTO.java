package com.cardio.cardiotrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MedicalRecordRequestDTO {

    @NotBlank(message = "Diagnosis is required")
    @Size(max = 200, message = "Diagnosis cannot exceed 200 characters")
    private String diagnosis;

    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;

    public MedicalRecordRequestDTO() {
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}