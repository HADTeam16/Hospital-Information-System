package org.had.hospitalinformationsystem.receptionist;


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
    Utils utils = new Utils();



    // Add Patient
    @PostMapping("/signup/patient")
    public AuthResponse signupPatient(@RequestHeader("Authorization") String jwt, @RequestBody RegistrationDto registrationDto) throws Exception {
        User newUser = utils.getUser(registrationDto);
        User savedUser;

        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if(role.equals("receptionist")  && registrationDto.getRole().equals("patient")){
            savedUser = userRepository.save(newUser);
            Patient newPatient = new Patient();
            newPatient.setUser(savedUser);
            newPatient.setTemperature(registrationDto.getTemperature());
            patientRepository.save(newPatient);
        }
        else{
            throw new Exception("Only Patient can be added by Receptionist");
        }
        Authentication authentication=new UsernamePasswordAuthenticationToken(savedUser.getUserName(),savedUser.getPassword());
        String token= JwtProvider.generateToken(authentication,newUser.getRole());
        return new AuthResponse(token,"Register Success",savedUser);
    }

    //Find Patient by UserName
    @GetMapping("/patient/username/{userName}")
    public Patient findPatientByUserName(@RequestHeader("Authorization") String jwt, @PathVariable String userName) throws Exception{
        Patient newPatient=null;
        String role = JwtProvider.getRoleFromJwtToken(jwt);

        if(role.equals("receptionist")){
            newPatient = patientRepository.findPatientByUserName(userName);
        }
        else{
            throw new Exception("Patient Does not exist");
        }
        return newPatient;
    }

    //Find Patient by Contact
    @GetMapping("/patient/contact/{contact}")
    public List<Patient> findPatientByContact(@RequestHeader("Authorization") String jwt, @PathVariable String contact) throws Exception{

        List<Patient> newPatient=null;
        String role = JwtProvider.getRoleFromJwtToken(jwt);

        if(role.equals("receptionist")){
            newPatient = patientRepository.findPatientByContact(contact);
        }
        else{
            throw new Exception("Patient Does not exist");
        }
        return newPatient;
    }

    // Find Doctor by Specialization
    @GetMapping("/doctor/{specialization}")
    public List<Doctor>findDoctorBySpecialization(@RequestHeader("Authorization") String jwt,@PathVariable String specialization) throws Exception{
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        List<Doctor>newDoctor = null;
        if(role.equals("receptionist")){
            newDoctor = doctorRepository.findDoctorBySpecialization(specialization);
        }
        else{
            throw new Exception("Doctor Does not exist");
        }
        return newDoctor;

    }

    // Find Doctor by name



}
