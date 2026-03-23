package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Appointment;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.AppointmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // Save doctor
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.existsByEmail(doctor.getEmail())) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // Update doctor
    public int updateDoctor(Doctor doctor) {
        if (!doctorRepository.existsById(doctor.getId())) {
            return -1;
        }
        doctorRepository.save(doctor);
        return 1;
    }

    // Delete doctor
    @Transactional
    public int deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) return -1;

        List<Appointment> appointments = appointmentRepository.findByDoctorId(id);
        appointmentRepository.deleteAll(appointments);

        doctorRepository.deleteById(id);
        return 1;
    }

    // Get all doctors
    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    // Find doctor by name
    @Transactional
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameContainingIgnoreCase(name);
    }

    // ✅ FIXED: Get doctor availability
    @Transactional
    public List<LocalTime> getDoctorAvailability(Long doctorId, LocalDate date) {

        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) return List.of();

        // Convert String times → LocalTime
        List<LocalTime> allSlots = doctor.getAvailableTimes()
                .stream()
                .map(LocalTime::parse)
                .collect(Collectors.toList());

        // Get start & end of day
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        // Booked slots
        List<LocalTime> booked = appointmentRepository
                .findByDoctorIdAndDateRange(doctorId, start, end)
                .stream()
                .map(Appointment::getAppointmentTime)
                .map(LocalDateTime::toLocalTime)
                .collect(Collectors.toList());

        // Return available slots
        return allSlots.stream()
                .filter(slot -> !booked.contains(slot))
                .collect(Collectors.toList());
    }

    // Validate login
    public String validateDoctor(String email, String password) {
        Doctor doctor = doctorRepository.findByEmail(email);
        if (doctor == null) return "Doctor not found";

        if (!doctor.getPassword().equals(password)) return "Incorrect password";

        return tokenService.generateToken(email);
    }

    // ================= FILTER METHODS =================

    public List<Doctor> filterDoctorsByNameSpecilityAndTime(String name, String specialty, String period) {
        List<Doctor> list = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyContainingIgnoreCase(name, specialty);

        return filterDoctorByTime(list, period);
    }

    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String period) {
        return doctors.stream()
                .filter(d -> d.getAvailableTimes().stream()
                        .map(LocalTime::parse) // ✅ FIXED
                        .anyMatch(t ->
                                (period.equalsIgnoreCase("AM") && t.getHour() < 12) ||
                                        (period.equalsIgnoreCase("PM") && t.getHour() >= 12)
                        ))
                .collect(Collectors.toList());
    }

    public List<Doctor> filterDoctorByNameAndTime(String name, String period) {
        List<Doctor> list = doctorRepository.findByNameContainingIgnoreCase(name);
        return filterDoctorByTime(list, period);
    }

    public List<Doctor> filterDoctorByNameAndSpecialty(String name, String specialty) {
        return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyContainingIgnoreCase(name, specialty);
    }

    public List<Doctor> filterDoctorByTimeAndSpecialty(String period, String specialty) {
        List<Doctor> list = doctorRepository.findBySpecialtyContainingIgnoreCase(specialty);
        return filterDoctorByTime(list, period);
    }

    public List<Doctor> filterDoctorBySpecialty(String specialty) {
        return doctorRepository.findBySpecialtyContainingIgnoreCase(specialty);
    }

    public List<Doctor> filterDoctorsByTime(String period) {
        List<Doctor> all = doctorRepository.findAll();
        return filterDoctorByTime(all, period);
    }
}