package org.had.hospitalinformationsystem.controller;

import org.had.hospitalinformationsystem.config.JwtProvider;
import org.had.hospitalinformationsystem.model.Doctor;
import org.had.hospitalinformationsystem.model.LoginRequest;
import org.had.hospitalinformationsystem.repository.DoctorRepository;
import org.had.hospitalinformationsystem.response.AuthResponse;
import org.had.hospitalinformationsystem.detailServiceImplementation.DoctorDetailsServiceImplementation;
import org.had.hospitalinformationsystem.detailServiceImplementation.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    UserDetailsServiceImplementation UserDetailsService;

    @Autowired
    DoctorDetailsServiceImplementation doctorDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;
    private Authentication authenticate(String userName, String password) {
        UserDetails userDetails = null;
        userDetails=doctorDetailsService.loadUserByUsername(userName);
        if(userDetails==null || !passwordEncoder.matches(password,userDetails.getPassword())){
            throw new BadCredentialsException("Invalid UserName or Password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }

    // Doctor LogIn
    @PostMapping("/auth/signin")
    public AuthResponse signin(@RequestBody LoginRequest loginRequest){
        Authentication authentication=authenticate(loginRequest.getUserName(),loginRequest.getPassword());
        String token= JwtProvider.generateToken(authentication,"doctor");
        return new AuthResponse(token,"Login Success");
    }

    // Get all Doctors
    @GetMapping("/allDoctors")
    public List<Doctor> getAllDoctors(@RequestHeader("Authorization") String jwt){
        return doctorRepository.findAll();
    }

    // Get Doctor by UserName
    @GetMapping("/doctor/{userName}")
    public  Doctor findUserByUserName(@RequestHeader("Authorization") String jwt,@PathVariable String userName){
        return doctorRepository.findByUserName(userName);
    }
}
