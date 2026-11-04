package com.cardio.cardiotrack.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cardio.cardiotrack.dto.PatientRequestDTO;
import com.cardio.cardiotrack.dto.PatientResponseDTO;
import com.cardio.cardiotrack.dto.PatientUpdateRequestDTO;
import com.cardio.cardiotrack.exception.ResourceNotFoundException;
import com.cardio.cardiotrack.model.Patient;
import com.cardio.cardiotrack.model.Role;
import com.cardio.cardiotrack.model.User;
import com.cardio.cardiotrack.repository.PatientRepository;
import com.cardio.cardiotrack.repository.UserRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    
    public PatientService(PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository=userRepository;
    }
    
    private PatientResponseDTO toResponseDTO(Patient patient) {
    	return new PatientResponseDTO(patient.getId(), patient.getName(), patient.getAge(),patient.getGender());
    }
    
    
    
    
    
    
    public PatientResponseDTO createPatient(PatientRequestDTO request) {

        Patient patient = new Patient();

        patient.setName(request.getName());
        patient.setAge(request.getAge());
        patient.setGender(request.getGender());

        Patient savedPatient = patientRepository.save(patient);

        return  toResponseDTO(savedPatient);
    }
    
   
    
    
    
    public List<PatientResponseDTO> getAllPatients(){
    return patientRepository.findAll().stream().map(this::toResponseDTO).toList();   
    }
    
    
    
    
    
    public PatientResponseDTO getPatientById(Long id) {
        Patient patient =patientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found with id: " + id));
        
        return toResponseDTO(patient);
    }
    
    
    
    
    
    public void deletePatient(Long id) {

        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }

        patientRepository.deleteById(id);
    }
    
    
    
    
    
    
    public PatientResponseDTO updatePatient(Long id, PatientUpdateRequestDTO request) {

        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        existingPatient.setName(request.getName());
        existingPatient.setAge(request.getAge());
        existingPatient.setGender(request.getGender());

        Patient updated= patientRepository.save(existingPatient);
        return toResponseDTO(updated);
        
    }
    
    
    
    
    
    public Patient createPatientProfile(
            Long userId,
            PatientRequestDTO request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId));

        if (patientRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException(
                    "Patient profile already exists for this user");
        }

        Patient patient = new Patient();
        patient.setName(request.getName());
        patient.setAge(request.getAge());
        patient.setGender(request.getGender());
        patient.setUser(user);

        return patientRepository.save(patient);
    }
    
    public Patient createPatientProfileByEmail(
            String email,
            PatientRequestDTO request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email));

        if (user.getRole() != Role.PATIENT) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Only patient users can create their own profile");
        }

        if (patientRepository.findByUserId(user.getId()).isPresent()) {
            throw new IllegalStateException(
                    "Patient profile already exists for this user");
        }

        Patient patient = new Patient();
        patient.setName(request.getName());
        patient.setAge(request.getAge());
        patient.setGender(request.getGender());
        patient.setUser(user);

        return patientRepository.save(patient);
    }
  
    
    
    public PatientResponseDTO getMyPatientProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient profile not found"));

        return toResponseDTO(patient);
    }
    
    
    
    public PatientResponseDTO updateMyPatientProfile(
            String email,
            PatientUpdateRequestDTO request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient profile not found"));

        patient.setName(request.getName());
        patient.setAge(request.getAge());
        patient.setGender(request.getGender());

        Patient updatedPatient = patientRepository.save(patient);

        return toResponseDTO(updatedPatient);
    }
}