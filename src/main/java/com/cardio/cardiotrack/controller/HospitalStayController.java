package com.cardio.cardiotrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cardio.cardiotrack.dto.HospitalStayRequestDTO;
import com.cardio.cardiotrack.dto.HospitalStayResponseDTO;
import com.cardio.cardiotrack.service.HospitalStayService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients/{patientId}/hospital-stays")
public class HospitalStayController {

    private final HospitalStayService hospitalStayService;

    public HospitalStayController(
            HospitalStayService hospitalStayService) {
        this.hospitalStayService = hospitalStayService;
    }

    @PostMapping
    public HospitalStayResponseDTO admitPatient(
            @PathVariable Long patientId,
           @Valid @RequestBody HospitalStayRequestDTO hospitalStay) {

        return hospitalStayService.admitPatient(
            patientId, hospitalStay
        );
    }

    @GetMapping
    public List<HospitalStayResponseDTO> getStaysByPatientId(
            @PathVariable Long patientId) {

        return hospitalStayService.getStaysByPatientId(patientId);
    }

    @PutMapping("/{stayId}/discharge")
    public HospitalStayResponseDTO dischargePatient(
            @PathVariable Long patientId,
            @PathVariable Long stayId) {

        return hospitalStayService.dischargePatient(patientId,stayId);
    }
}