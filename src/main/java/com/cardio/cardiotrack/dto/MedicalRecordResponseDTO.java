package com.cardio.cardiotrack.dto;

import java.time.LocalDateTime;

public class MedicalRecordResponseDTO {

    private Long id;
    private String diagnosis;
    private String notes;
    private LocalDateTime recordedAt;
    private Long patientId;

    public MedicalRecordResponseDTO() {
    }

    public MedicalRecordResponseDTO(
            Long id,
            String diagnosis,
            String notes,
            LocalDateTime recordedAt,
            Long patientId) {
        this.id = id;
        this.diagnosis = diagnosis;
        this.notes = notes;
        this.recordedAt = recordedAt;
        this.patientId = patientId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
}