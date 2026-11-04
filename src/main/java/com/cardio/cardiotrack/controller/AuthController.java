package com.cardio.cardiotrack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cardio.cardiotrack.dto.AuthResponseDTO;
import com.cardio.cardiotrack.dto.LoginRequestDTO;
import com.cardio.cardiotrack.dto.RegisterRequestDTO;
import com.cardio.cardiotrack.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registerUser(
            @Valid @RequestBody RegisterRequestDTO requestDTO) {

        AuthResponseDTO response = userService.registerUser(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(
            @Valid @RequestBody LoginRequestDTO requestDTO) {

        AuthResponseDTO response = userService.loginUser(requestDTO);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/admin/doctors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponseDTO> createDoctorAccount(
            @Valid @RequestBody RegisterRequestDTO requestDTO) {

        AuthResponseDTO response =
                userService.createDoctorAccount(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}