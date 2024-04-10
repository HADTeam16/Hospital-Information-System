package org.had.hospitalinformationsystem.receptionist;


import org.had.hospitalinformationsystem.consent.Consent;
import org.had.hospitalinformationsystem.consent.ConsentRepository;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.dto.AuthResponse;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.had.hospitalinformationsystem.nurse.Nurse;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.utility.Utils;
import org.had.hospitalinformationsystem.ward.WardController;
import org.had.hospitalinformationsystem.ward.WardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @Autowired
    WardService wardService;

    @Autowired
    ReceptionistRepository receptionistRepository;
    // Add Patient
    @PostMapping("/signup/patient")
    public ResponseEntity<Object> signupPatient(@RequestHeader("Authorization") String jwt, @RequestBody RegistrationDto registrationDto) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("receptionist")){
                Object result = utils.getUser(registrationDto);
                if(result instanceof String){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponse(null,(String) result,null));
                }
                else{
                    User newUser = (User) result;
                    newUser.setDisable(true);
                    newUser.getAuth().setPassword("");
                    Patient newPatient = new Patient();
                    newPatient.setUser(newUser);
                    newPatient.setTemperature(registrationDto.getTemperature());
                    newPatient.setBloodPressure(registrationDto.getBloodPressure());
                    newPatient.setHeight(registrationDto.getHeight());
                    newPatient.setWeight(registrationDto.getWeight());
                    newPatient.setRegistrationDateAndTime(LocalDateTime.now());
                    Consent currPatientConsent = new Consent();
                    currPatientConsent.setPatient(newPatient);
                    currPatientConsent.setConcent(true);
                    userRepository.save(newUser);
                    patientRepository.save(newPatient);
                    consentRepository.save(currPatientConsent);
                    return ResponseEntity.ok(new AuthResponse("", "Register Success", newUser));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null,"Access denied",null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during patient registration: " + e.getMessage());
        }
    }
    
    @GetMapping("/find/doctor/by/specialization/{specialization}")
    public ResponseEntity<?> findDoctorBySpecialization(@RequestHeader("Authorization") String jwt, @PathVariable String specialization) {
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
    @GetMapping("/create/ward")
    public ResponseEntity<String> createWard(@RequestHeader("Authorization") String jwt){
        String role=JwtProvider.getRoleFromJwtToken(jwt);
        if(role.equals("receptionist")){
            wardService.createInitialWards();
            return ResponseEntity.ok("Wards created successfully");
        }
        else{
            return ResponseEntity.badRequest().body("Only Receptionist can create wards");
        }
    }
    @GetMapping("/get/all/receptionist")
    public ResponseEntity<List<Receptionist>> getAllReceptionist(@RequestHeader("Authorization") String jwt){
        String userName=JwtProvider.getUserNameFromJwtToken(jwt);
        User user=userRepository.findByUserName(userName);
        if(user.getRole().equals("admin")){
            List<Receptionist> receptionists=receptionistRepository.findAll();
            return ResponseEntity.ok().body(receptionists);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

}
