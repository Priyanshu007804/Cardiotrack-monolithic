package com.cardio.cardiotrack.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.cardio.cardiotrack.dto.AssignmentResponseDTO;
import com.cardio.cardiotrack.service.DoctorPatientAssignmentService;

@RestController
@RequestMapping("/api/assignments")
public class DoctorPatientAssignmentController {

    private final DoctorPatientAssignmentService assignmentService;

    public DoctorPatientAssignmentController(
            DoctorPatientAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public AssignmentResponseDTO assignDoctorToPatient(
            @RequestParam Long doctorId,
            @RequestParam Long patientId) {

        return assignmentService.assignDoctorToPatient(
                doctorId, patientId);
    }

    @GetMapping("/me/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<AssignmentResponseDTO> getMyDoctorAssignments(
            Authentication authentication) {

        return assignmentService.getMyAssignmentsAsDoctor(
                authentication.getName()
        );
    }

    @GetMapping("/me/patient")
    @PreAuthorize("hasRole('PATIENT')")
    public List<AssignmentResponseDTO> getMyPatientAssignments(
            Authentication authentication) {

        return assignmentService.getMyAssignmentsAsPatient(
                authentication.getName()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<AssignmentResponseDTO> getAllActiveAssignments() {
        return assignmentService.getAllActiveAssignments();
    }

    @PatchMapping("/{assignmentId}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateAssignment(
            @PathVariable Long assignmentId) {

        assignmentService.deactivateAssignment(assignmentId);
    }
}
