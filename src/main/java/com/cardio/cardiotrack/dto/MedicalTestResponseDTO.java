package com.cardio.cardiotrack.dto;

import java.time.LocalDateTime;

public class MedicalTestResponseDTO {

    private Long id;
    private Long patientId;
    private Long hospitalStayId;
    private String testName;
    private String testType;
    private LocalDateTime testDate;
    private String results;
    private String interpretation;
    private LocalDateTime createdAt;

    public MedicalTestResponseDTO() {
    }

    public MedicalTestResponseDTO(
            Long id,
            Long patientId,
            Long hospitalStayId,
            String testName,
            String testType,
            LocalDateTime testDate,
            String results,
            String interpretation,
            LocalDateTime createdAt) {

        this.id = id;
        this.patientId = patientId;
        this.hospitalStayId = hospitalStayId;
        this.testName = testName;
        this.testType = testType;
        this.testDate = testDate;
        this.results = results;
        this.interpretation = interpretation;
        this.createdAt = createdAt;
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

    public Long getHospitalStayId() {
        return hospitalStayId;
    }

    public void setHospitalStayId(Long hospitalStayId) {
        this.hospitalStayId = hospitalStayId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public LocalDateTime getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDateTime testDate) {
        this.testDate = testDate;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(String interpretation) {
        this.interpretation = interpretation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}