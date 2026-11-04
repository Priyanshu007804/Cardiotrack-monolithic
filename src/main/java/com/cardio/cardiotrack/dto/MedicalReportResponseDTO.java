package com.cardio.cardiotrack.dto;

import java.time.LocalDateTime;

public class MedicalReportResponseDTO {

    private Long id;
    private Long patientId;
    private Long medicalTestId;
    private String fileName;
    private String contentType;
    private Long fileSizeBytes;
    private LocalDateTime uploadedAt;
    private Long uploadedById;

    public MedicalReportResponseDTO() {
    }

    public MedicalReportResponseDTO(
            Long id,
            Long patientId,
            Long medicalTestId,
            String fileName,
            String contentType,
            Long fileSizeBytes,
            LocalDateTime uploadedAt,
            Long uploadedById) {

        this.id = id;
        this.patientId = patientId;
        this.medicalTestId = medicalTestId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSizeBytes = fileSizeBytes;
        this.uploadedAt = uploadedAt;
        this.uploadedById = uploadedById;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getMedicalTestId() {
        return medicalTestId;
    }

    public void setMedicalTestId(Long medicalTestId) {
        this.medicalTestId = medicalTestId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Long getUploadedById() {
        return uploadedById;
    }

    public void setUploadedById(Long uploadedById) {
        this.uploadedById = uploadedById;
    }
}