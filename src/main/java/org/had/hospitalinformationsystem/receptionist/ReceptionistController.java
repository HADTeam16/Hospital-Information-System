package org.had.hospitalinformationsystem.receptionist;


import org.had.hospitalinformationsystem.consent.Consent;
import org.had.hospitalinformationsystem.consent.ConsentRepository;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.dto.AuthResponse;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receptionist")
public class ReceptionistController {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    ConsentRepository consentRepository;

    @Autowired
    Utils utils = new Utils();



    // Add Patient
    @PostMapping("/signup/patient")
    public ResponseEntity<?> signupPatient(@RequestHeader("Authorization") String jwt, @RequestBody RegistrationDto registrationDto) {
        try {
            User newUser = utils.getUser(registrationDto);
            User savedUser;
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (!role.equals("receptionist") || !registrationDto.getRole().equals("patient")) {
                throw new Exception("Access Denied!!");
            }
            savedUser = userRepository.save(newUser);
            Patient newPatient = new Patient();
            newPatient.setUser(savedUser);
            patientRepository.save(newPatient);
            Consent currPatientConsent = new Consent();
            currPatientConsent.setPatient(newPatient);
            currPatientConsent.setConcent(true);
            consentRepository.save(currPatientConsent);
            Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getUserName(), savedUser.getPassword());
            String token = JwtProvider.generateToken(authentication, newUser.getRole());
            return ResponseEntity.ok(new AuthResponse(token, "Register Success", savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during patient registration: " + e.getMessage());
        }
    }

    // Find Doctor by Specialization
    @GetMapping("/doctor")
    public ResponseEntity<?> findDoctorBySpecialization(@RequestHeader("Authorization") String jwt, @RequestBody String specialization) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (!role.equals("receptionist")) {
                throw new Exception("Access Denied - Only receptionists can access this information.");
            }

            List<Doctor> doctors = doctorRepository.findDoctorBySpecialization(specialization);
            if (doctors.isEmpty()) {
                throw new Exception("Doctor with the specialization '" + specialization + "' does not exist.");
            }

            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error finding doctor by specialization: " + e.getMessage());
        }
    }
}
