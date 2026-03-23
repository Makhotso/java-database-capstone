package com.project.back_end.services;

import com.project.back_end.models.Patient;
import com.project.back_end.models.Appointment;
import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // Create a new patient
    public boolean createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if patient exists by email
    public boolean patientExists(String email) {
        return patientRepository.findByEmail(email) != null;
    }

    // Validate patient login
    public String validatePatientLogin(String email, String password) {
        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) return "Patient not found";
        if (!patient.getPassword().equals(password)) return "Incorrect password";
        return tokenService.generateToken(email);
    }

    // Get patient by token
    public Patient getPatientByToken(String token) {
        try {
            String email = tokenService.extractEmail(token);
            return patientRepository.findByEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get all appointments for a patient
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
    }

    // Filter appointments by condition (past/future)
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterAppointmentsByCondition(String condition, Long patientId) {
        int status;
        if ("past".equalsIgnoreCase(condition)) status = 1;
        else if ("future".equalsIgnoreCase(condition)) status = 0;
        else return List.of();

        return appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, status)
                .stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
    }

    // Filter appointments by doctor name
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterAppointmentsByDoctor(Long patientId, String doctorName) {
        return appointmentRepository.filterByDoctorNameAndPatientId(doctorName, patientId)
                .stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
    }

    // Filter appointments by doctor name + condition
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterAppointmentsByDoctorAndCondition(Long patientId, String doctorName, String condition) {
        int status;
        if ("past".equalsIgnoreCase(condition)) status = 1;
        else if ("future".equalsIgnoreCase(condition)) status = 0;
        else return List.of();

        return appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status)
                .stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
    }
}