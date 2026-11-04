package com.cardio.cardiotrack.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public class MedicalTestRequestDTO {

    @NotBlank(message = "Test name is required")
    @Size(max = 150, message = "Test name cannot exceed 150 characters")
    private String testName;

    @NotBlank(message = "Test type is required")
    @Size(max = 100, message = "Test type cannot exceed 100 characters")
    private String testType;

    @NotNull(message = "Test date is required")
    @PastOrPresent(message = "Test date cannot be in the future")
    private LocalDateTime testDate;

    @Size(max = 5000, message = "Results cannot exceed 5000 characters")
    private String results;

    @Size(max = 5000, message = "Interpretation cannot exceed 5000 characters")
    private String interpretation;

    public MedicalTestRequestDTO() {
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
}