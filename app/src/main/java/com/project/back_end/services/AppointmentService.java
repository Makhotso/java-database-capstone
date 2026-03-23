package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // 1. Book Appointment
    @Transactional
    public boolean bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 2. Update Appointment
    @Transactional
    public boolean updateAppointment(Long id, Appointment updatedAppointment, Long patientId) {
        Optional<Appointment> existing = appointmentRepository.findById(id);
        if (existing.isEmpty()) return false;

        Appointment appointment = existing.get();

        if (!appointment.getPatient().getId().equals(patientId)) return false;

        appointment.setAppointmentTime(updatedAppointment.getAppointmentTime());
        appointment.setStatus(updatedAppointment.getStatus());

        appointmentRepository.save(appointment);
        return true;
    }

    // 3. Cancel Appointment
    @Transactional
    public boolean cancelAppointment(Long id, Long patientId) {
        Optional<Appointment> existing = appointmentRepository.findById(id);
        if (existing.isEmpty()) return false;

        Appointment appointment = existing.get();

        if (!appointment.getPatient().getId().equals(patientId)) return false;

        appointmentRepository.delete(appointment);
        return true;
    }

    // 4. Get All Appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // 5. Change Status
    @Transactional
    public boolean changeStatus(Long id, int status) {
        Optional<Appointment> existing = appointmentRepository.findById(id);
        if (existing.isEmpty()) return false;

        Appointment appointment = existing.get();
        appointment.setStatus(status);

        appointmentRepository.save(appointment);
        return true;
    }
}