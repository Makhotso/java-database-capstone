package com.project.back_end.controllers;

import com.project.back_end.models.Patient;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.TokenService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final PatientService patientService;
    private final TokenService tokenService;

    public PatientController(PatientService patientService,
                             TokenService tokenService) {
        this.patientService = patientService;
        this.tokenService = tokenService;
    }

    // Get patient info by token
    @GetMapping("/info/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        Patient patient = patientService.getPatientByToken(token);
        if (patient == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
        return ResponseEntity.ok(patient);
    }

    // Create a new patient
    @PostMapping("/register")
    public ResponseEntity<String> createPatient(@RequestBody Patient patient) {
        if (patientService.patientExists(patient.getEmail()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Patient already exists");

        if (!patientService.createPatient(patient))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating patient");

        return ResponseEntity.status(HttpStatus.CREATED).body("Patient created successfully");
    }

    // Patient login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Login login) {
        String tokenOrMessage = patientService.validatePatientLogin(login.getEmail(), login.getPassword());

        if (tokenOrMessage.equals("Patient not found") || tokenOrMessage.equals("Incorrect password")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(tokenOrMessage);
        }
        return ResponseEntity.ok(tokenOrMessage);
    }

    // Get all appointments
    @GetMapping("/appointments/{patientId}/{token}/{role}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable Long patientId,
                                                    @PathVariable String token,
                                                    @PathVariable String role) {
        if (!tokenService.validateToken(token, role))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");

        List<?> appointments = patientService.getPatientAppointments(patientId);
        return ResponseEntity.ok(appointments);
    }

    // Filter appointments by condition only
    @GetMapping("/appointments/filter/{condition}/{patientId}/{token}")
    public ResponseEntity<?> filterAppointmentsByCondition(@PathVariable String condition,
                                                           @PathVariable Long patientId,
                                                           @PathVariable String token) {
        if (!tokenService.validateToken(token, "patient"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");

        List<?> filteredAppointments = patientService.filterAppointmentsByCondition(condition, patientId);
        return ResponseEntity.ok(filteredAppointments);
    }

    // Filter appointments by doctor name
    @GetMapping("/appointments/filter/doctor/{doctorName}/{patientId}/{token}")
    public ResponseEntity<?> filterAppointmentsByDoctor(@PathVariable String doctorName,
                                                        @PathVariable Long patientId,
                                                        @PathVariable String token) {
        if (!tokenService.validateToken(token, "patient"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");

        List<?> filteredAppointments = patientService.filterAppointmentsByDoctor(patientId, doctorName);
        return ResponseEntity.ok(filteredAppointments);
    }

    // Filter appointments by doctor + condition
    @GetMapping("/appointments/filter/doctor/{doctorName}/{condition}/{patientId}/{token}")
    public ResponseEntity<?> filterAppointmentsByDoctorAndCondition(@PathVariable String doctorName,
                                                                    @PathVariable String condition,
                                                                    @PathVariable Long patientId,
                                                                    @PathVariable String token) {
        if (!tokenService.validateToken(token, "patient"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");

        List<?> filteredAppointments = patientService.filterAppointmentsByDoctorAndCondition(patientId, doctorName, condition);
        return ResponseEntity.ok(filteredAppointments);
    }
}