package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.TokenService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final AppointmentService appointmentService;
    private final TokenService tokenService;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  AppointmentService appointmentService,
                                  TokenService tokenService) {
        this.prescriptionService = prescriptionService;
        this.appointmentService = appointmentService;
        this.tokenService = tokenService;
    }

    // Save Prescription (Doctor only)
    @PostMapping("/add/{token}")
    public ResponseEntity<String> savePrescription(@RequestBody Prescription prescription,
                                                   @PathVariable String token) {
        if (!tokenService.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: invalid token");
        }

        // Save prescription
        boolean saved = prescriptionService.savePrescription(prescription);
        if (!saved) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving prescription");
        }

        // Update appointment status (returns boolean now)
        boolean statusUpdated = appointmentService.changeStatus(prescription.getAppointmentId(), 1); // 1 = Completed
        if (!statusUpdated) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Prescription saved but failed to update appointment status");
        }

        return ResponseEntity.ok("Prescription saved successfully");
    }

    // Get Prescription by Appointment ID (Doctor only)
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable Long appointmentId,
                                             @PathVariable String token) {
        if (!tokenService.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: invalid token");
        }

        Prescription prescription = prescriptionService.getPrescriptionByAppointmentId(appointmentId);
        if (prescription == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Prescription not found for this appointment");
        }

        return ResponseEntity.ok(prescription);
    }
}