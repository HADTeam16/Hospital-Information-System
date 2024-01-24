package org.had.hospitalinformationsystem.controller;

import org.had.hospitalinformationsystem.config.JwtProvider;
import org.had.hospitalinformationsystem.model.Doctor;
import org.had.hospitalinformationsystem.model.User;
import org.had.hospitalinformationsystem.repository.DoctorRepository;
import org.had.hospitalinformationsystem.repository.UserRepository;
import org.had.hospitalinformationsystem.request.LoginRequest;
import org.had.hospitalinformationsystem.response.AuthResponse;
import org.had.hospitalinformationsystem.service.DoctorService;
import org.had.hospitalinformationsystem.service.UserService;
import org.had.hospitalinformationsystem.serviceImpl.CustomerUserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    DoctorService doctorService;


    //Get details of all users
    @GetMapping("/allUsers")
    public List<Doctor>getAllDoctors(){
        return doctorRepository.findAll();
    }

    //Get User details by Id
    @GetMapping("/{id}")
    public Doctor findUser(@PathVariable Long id) throws Exception {
        Doctor doctor;
        doctor = doctorService.findDoctorById(id);
        return  doctor;

    }

//    @PutMapping("/updateUser/{id}")
//    public User updateUser(@RequestBody User user){
//        try{
//            User newUser = userRepository.findAllById(user.getId());
//        }catch(){
//
//        }
//    }



}
