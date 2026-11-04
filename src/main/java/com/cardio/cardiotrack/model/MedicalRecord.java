package com.cardio.cardiotrack.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name="mediacal_record")
public class MedicalRecord {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	
	private String diagnosis;
	private String notes;
	private LocalDateTime recordedAt;
	
	@ManyToOne
	@JoinColumn(name="patient_id", nullable= false)
	private Patient patient;
	
	public MedicalRecord() {
		
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
