package com.cardio.cardiotrack.service;

import com.cardio.cardiotrack.repository.DoctorRepository;

import com.cardio.cardiotrack.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.cardio.cardiotrack.dto.DoctorRequestDTO;
import com.cardio.cardiotrack.dto.DoctorResponseDTO;
import com.cardio.cardiotrack.dto.DoctorUpdateRequestDTO;
import com.cardio.cardiotrack.exception.ResourceNotFoundException;
import com.cardio.cardiotrack.model.Doctor;
import com.cardio.cardiotrack.model.Role;
import com.cardio.cardiotrack.model.User;
import org.springframework.transaction.annotation.Transactional;
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public DoctorService(
            DoctorRepository doctorRepository,
            UserRepository userRepository) {

        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }
    
    
    @Transactional
    public DoctorResponseDTO createDoctorProfile(
            Long userId,
            DoctorRequestDTO request) {

        // 1. Find the existing user
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + userId));

        // 2. Ensure the user has the DOCTOR role
        if (user.getRole() != Role.DOCTOR) {
            throw new IllegalArgumentException(
                    "User must have the DOCTOR role");
        }

        // 3. Prevent duplicate doctor profiles
        if (doctorRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException(
                    "Doctor profile already exists for this user");
        }

        // 4. Prevent duplicate registration numbers
        if (doctorRepository.existsByRegistrationNumber(
                request.registrationNumber().trim())) {
            throw new IllegalStateException(
                    "Registration number already exists");
        }

        // 5. Create and populate the Doctor entity
        Doctor doctor = new Doctor();

        doctor.setFullName(request.fullName().trim());
        doctor.setQualification(request.qualification().trim());
        doctor.setSpecialization(request.specialization().trim());
        doctor.setExperienceYears(request.experienceYears());
        doctor.setRegistrationNumber(
                request.registrationNumber().trim());

        // 6. Link the existing User account
        doctor.setUser(user);

        // 7. Save the profile
        Doctor savedDoctor = doctorRepository.save(doctor);

        // 8. Return a DTO instead of the entity
        return new DoctorResponseDTO(
                savedDoctor.getId(),
                savedDoctor.getFullName(),
                savedDoctor.getQualification(),
                savedDoctor.getSpecialization(),
                savedDoctor.getExperienceYears(),
                savedDoctor.getRegistrationNumber()
        );
    }
    
    @Transactional(readOnly = true)
    public DoctorResponseDTO getMyDoctorProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email
                ));

        if (user.getRole() != Role.DOCTOR) {
            throw new IllegalArgumentException(
                    "Only doctors can access doctor profiles"
            );
        }

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor profile not found"
                ));

        return new DoctorResponseDTO(
                doctor.getId(),
                doctor.getFullName(),
                doctor.getQualification(),
                doctor.getSpecialization(),
                doctor.getExperienceYears(),
                doctor.getRegistrationNumber()
        );
    }
    
    @Transactional
    public DoctorResponseDTO updateMyDoctorProfile(
            String email,
            DoctorUpdateRequestDTO request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email
                ));

        if (user.getRole() != Role.DOCTOR) {
            throw new IllegalArgumentException(
                    "Only doctors can update doctor profiles"
            );
        }

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor profile not found"
                ));

        doctor.setFullName(request.fullName().trim());
        doctor.setQualification(request.qualification().trim());
        doctor.setSpecialization(request.specialization().trim());
        doctor.setExperienceYears(request.experienceYears());

        Doctor updatedDoctor = doctorRepository.save(doctor);

        return new DoctorResponseDTO(
                updatedDoctor.getId(),
                updatedDoctor.getFullName(),
                updatedDoctor.getQualification(),
                updatedDoctor.getSpecialization(),
                updatedDoctor.getExperienceYears(),
                updatedDoctor.getRegistrationNumber()
        );
    }
}