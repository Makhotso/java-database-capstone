package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.TokenService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final TokenService tokenService;

    public AppointmentController(AppointmentService appointmentService,
                                 TokenService tokenService) {
        this.appointmentService = appointmentService;
        this.tokenService = tokenService;
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable String token) {
        if (!tokenService.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        List<Appointment> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @PostMapping("/{token}")
    public ResponseEntity<String> bookAppointment(@RequestBody Appointment appointment,
                                                  @PathVariable String token) {
        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        boolean booked = appointmentService.bookAppointment(appointment);

        if (booked) {
            return ResponseEntity.ok("Appointment booked successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to book appointment");
        }
    }

    @PutMapping("/{id}/{patientId}/{token}")
    public ResponseEntity<String> updateAppointment(@PathVariable Long id,
                                                    @PathVariable Long patientId,
                                                    @RequestBody Appointment appointment,
                                                    @PathVariable String token) {
        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        boolean updated = appointmentService.updateAppointment(id, appointment, patientId);

        if (updated) {
            return ResponseEntity.ok("Appointment updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update appointment");
        }
    }

    @DeleteMapping("/{id}/{patientId}/{token}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long id,
                                                    @PathVariable Long patientId,
                                                    @PathVariable String token) {
        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        boolean canceled = appointmentService.cancelAppointment(id, patientId);

        if (canceled) {
            return ResponseEntity.ok("Appointment canceled successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to cancel appointment");
        }
    }

    @PutMapping("/status/{id}/{status}/{token}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id,
                                               @PathVariable int status,
                                               @PathVariable String token) {
        if (!tokenService.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        boolean updated = appointmentService.changeStatus(id, status);

        if (updated) {
            return ResponseEntity.ok("Appointment status updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update appointment status");
        }
    }
}