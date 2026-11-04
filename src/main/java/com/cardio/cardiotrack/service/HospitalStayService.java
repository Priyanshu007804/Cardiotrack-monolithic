package com.cardio.cardiotrack.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cardio.cardiotrack.dto.HospitalStayRequestDTO;
import com.cardio.cardiotrack.dto.HospitalStayResponseDTO;
import com.cardio.cardiotrack.exception.ResourceNotFoundException;
import com.cardio.cardiotrack.model.HospitalStay;
import com.cardio.cardiotrack.model.HospitalStayStatus;
import com.cardio.cardiotrack.model.Patient;
import com.cardio.cardiotrack.repository.HospitalStayRepository;
import com.cardio.cardiotrack.repository.PatientRepository;

@Service
public class HospitalStayService {

    private final HospitalStayRepository hospitalStayRepository;
    private final PatientRepository patientRepository;

    public HospitalStayService(
            HospitalStayRepository hospitalStayRepository,
            PatientRepository patientRepository) {
        this.hospitalStayRepository = hospitalStayRepository;
        this.patientRepository = patientRepository;
    }

    public HospitalStayResponseDTO admitPatient(
            Long patientId,
            HospitalStayRequestDTO requestDTO) {

        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Patient not found with id: " + patientId
            ));

        HospitalStay hospitalStay = new HospitalStay();

        hospitalStay.setPatient(patient);
        hospitalStay.setAdmissionReason(requestDTO.getAdmissionReason());
        hospitalStay.setNotes(requestDTO.getNotes());

        hospitalStay.setAdmissionDate(LocalDateTime.now());
        hospitalStay.setDischargeDate(null);
        hospitalStay.setStatus(HospitalStayStatus.ADMITTED);

        HospitalStay savedStay = hospitalStayRepository.save(hospitalStay);

        return toResponseDTO(savedStay);
    }
    
    
    
    public List<HospitalStayResponseDTO> getStaysByPatientId(Long patientId) {

        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException(
                "Patient not found with id: " + patientId
            );
        }

        return hospitalStayRepository.findByPatientId(patientId)
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }
    
    
    

    public HospitalStayResponseDTO dischargePatient(Long patientId,Long stayId) {

        HospitalStay stay = hospitalStayRepository.findById(stayId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Hospital stay not found with id: " + stayId
                )
            );
            
            if (!stay.getPatient().getId().equals(patientId)) {
                throw new ResourceNotFoundException(
                    "Hospital stay does not belong to patient with id: " + patientId
                );
            };

        if (stay.getStatus() == HospitalStayStatus.DISCHARGED) {
            throw new IllegalStateException(
                "Patient has already been discharged."
            );
        }

        stay.setDischargeDate(LocalDateTime.now());
        stay.setStatus(HospitalStayStatus.DISCHARGED);

        HospitalStay updatedStay=hospitalStayRepository.save(stay);
        return toResponseDTO(updatedStay);
    }
    
    
    
    
    
    private HospitalStayResponseDTO toResponseDTO(HospitalStay stay) {
        return new HospitalStayResponseDTO(
            stay.getId(),
            stay.getPatient().getId(),
            stay.getAdmissionDate(),
            stay.getDischargeDate(),
            stay.getAdmissionReason(),
            stay.getNotes(),
            stay.getStatus()
        );
    }
}