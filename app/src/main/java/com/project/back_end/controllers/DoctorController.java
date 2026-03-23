package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.TokenService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final TokenService tokenService;

    // Constructor injection
    public DoctorController(DoctorService doctorService,
                            TokenService tokenService) {
        this.doctorService = doctorService;
        this.tokenService = tokenService;
    }

    // 1. Get Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(@PathVariable String user,
                                                   @PathVariable Long doctorId,
                                                   @PathVariable String date,
                                                   @PathVariable String token) {

        if (!tokenService.validateToken(token, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        LocalDate localDate = LocalDate.parse(date);
        List<?> availableTimes = doctorService.getDoctorAvailability(doctorId, localDate);
        return ResponseEntity.ok(availableTimes);
    }

    // 2. Get All Doctors
    @GetMapping("/all")
    public ResponseEntity<Map<String, List<Doctor>>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        Map<String, List<Doctor>> response = new HashMap<>();
        response.put("doctors", doctors);
        return ResponseEntity.ok(response);
    }

    // 3. Save Doctor (Admin only)
    @PostMapping("/add/{token}")
    public ResponseEntity<String> saveDoctor(@RequestBody Doctor doctor,
                                             @PathVariable String token) {

        if (!tokenService.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        int result = doctorService.saveDoctor(doctor);
        if (result == -1) return ResponseEntity.status(HttpStatus.CONFLICT).body("Doctor already exists");
        if (result == 0) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving doctor");

        return ResponseEntity.ok("Doctor added successfully");
    }

    // 4. Doctor Login
    @PostMapping("/login")
    public ResponseEntity<String> doctorLogin(@RequestBody Login login) {
        String tokenOrMessage = doctorService.validateDoctor(login.getEmail(), login.getPassword());

        if (tokenOrMessage.equals("Doctor not found") || tokenOrMessage.equals("Incorrect password")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(tokenOrMessage);
        }

        return ResponseEntity.ok(tokenOrMessage);
    }

    // 5. Update Doctor (Admin only)
    @PutMapping("/update/{token}")
    public ResponseEntity<String> updateDoctor(@RequestBody Doctor doctor,
                                               @PathVariable String token) {

        if (!tokenService.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        int result = doctorService.updateDoctor(doctor);
        if (result == -1) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found");

        return ResponseEntity.ok("Doctor updated successfully");
    }

    // 6. Delete Doctor (Admin only)
    @DeleteMapping("/delete/{id}/{token}")
    public ResponseEntity<String> deleteDoctor(@PathVariable Long id,
                                               @PathVariable String token) {

        if (!tokenService.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        int result = doctorService.deleteDoctor(id);
        if (result == -1) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found");

        return ResponseEntity.ok("Doctor deleted successfully");
    }

    // 7. Filter Doctors
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<List<Doctor>> filterDoctors(@PathVariable String name,
                                                      @PathVariable String time,
                                                      @PathVariable String speciality) {

        List<Doctor> filtered = doctorService.filterDoctorsByNameSpecilityAndTime(name, speciality, time);
        return ResponseEntity.ok(filtered);
    }
}

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.


// 2. Autowire Dependencies:
//    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
//    - Inject the shared `Service` class for general-purpose features like token validation and filtering.


// 3. Define the `getDoctorAvailability` Method:
//    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
//    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
//    - First validates the token against the user type.
//    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.


// 4. Define the `getDoctor` Method:
//    - Handles HTTP GET requests to retrieve a list of all doctors.
//    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.


// 5. Define the `saveDoctor` Method:
//    - Handles HTTP POST requests to register a new doctor.
//    - Accepts a validated `Doctor` object in the request body and a token for authorization.
//    - Validates the token for the `"admin"` role before proceeding.
//    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.


// 6. Define the `doctorLogin` Method:
//    - Handles HTTP POST requests for doctor login.
//    - Accepts a validated `Login` DTO containing credentials.
//    - Delegates authentication to the `DoctorService` and returns login status and token information.


// 7. Define the `updateDoctor` Method:
//    - Handles HTTP PUT requests to update an existing doctor's information.
//    - Accepts a validated `Doctor` object and a token for authorization.
//    - Token must belong to an `"admin"`.
//    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.


// 8. Define the `deleteDoctor` Method:
//    - Handles HTTP DELETE requests to remove a doctor by ID.
//    - Requires both doctor ID and an admin token as path variables.
//    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.


// 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `speciality` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.



