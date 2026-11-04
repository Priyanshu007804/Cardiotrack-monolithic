package com.cardio.cardiotrack.dto;

import java.time.LocalDateTime;

import com.cardio.cardiotrack.model.HospitalStayStatus;

public class HospitalStayResponseDTO {

    private Long id;
    private Long patientId;
    private LocalDateTime admissionDate;
    private LocalDateTime dischargeDate;
    private String admissionReason;
    private String notes;
    private HospitalStayStatus status;

    public HospitalStayResponseDTO() {
    }

    public HospitalStayResponseDTO(
            Long id,
            Long patientId,
            LocalDateTime admissionDate,
            LocalDateTime dischargeDate,
            String admissionReason,
            String notes,
            HospitalStayStatus status) {

        this.id = id;
        this.patientId = patientId;
        this.admissionDate = admissionDate;
        this.dischargeDate = dischargeDate;
        this.admissionReason = admissionReason;
        this.notes = notes;
        this.status = status;
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

    public LocalDateTime getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDateTime admissionDate) {
        this.admissionDate = admissionDate;
    }

    public LocalDateTime getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(LocalDateTime dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

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

    public HospitalStayStatus getStatus() {
        return status;
    }

    public void setStatus(HospitalStayStatus status) {
        this.status = status;
    }
}