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
import org.had.hospitalinformationsystem.serviceImpl.UserDetailsServiceImplementation;
import org.had.hospitalinformationsystem.serviceImpl.DoctorDetailsServiceImplementation;
import org.had.hospitalinformationsystem.serviceImpl.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    DoctorService doctorService;

    @Autowired
    UserDetailsServiceImplementation UserDetailsService;

    @Autowired
    DoctorDetailsServiceImplementation doctorDetailsService;

    @PostMapping("/signup/user")
    public AuthResponse createUser(@RequestBody User user) throws Exception {
        User isExist= userRepository.findByUserName(user.getUserName());
        if(isExist!=null){
            throw new Exception("Account already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser=userRepository.save(user);
        Authentication authentication=new UsernamePasswordAuthenticationToken(savedUser.getUserName(),savedUser.getPassword());
        String token= JwtProvider.generateToken(authentication,user.getRole());
        AuthResponse res=new AuthResponse(token,"Register Success");

        return res;
    }
    private Authentication authenticate(String userName, String password,String role) {
        UserDetails userDetails = null;
        if("doctor".equals(role)){
            userDetails=doctorDetailsService.loadUserByUsername(userName);
            if(userDetails==null){
                throw new BadCredentialsException("invalid username");
            }
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("password not matched");
            }
        }
        if("nurse".equals(role)){
            userDetails=UserDetailsService.loadUserByUsername(userName);
            if(userDetails==null){
                throw new BadCredentialsException("invalid username");
            }
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("password not matched");
            }
        }


        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }
    @PostMapping("/signin")
    public AuthResponse signin(@RequestBody LoginRequest loginRequest){
        Authentication authentication=authenticate(loginRequest.getUserName(),loginRequest.getPassword(),loginRequest.getRole());
        String token= JwtProvider.generateToken(authentication,loginRequest.getRole());
        AuthResponse res=new AuthResponse(token,"Login Success");

        return res;
    }
    @PostMapping("/signup/doctor")
    public AuthResponse createDoctor(@RequestBody Doctor doctor) throws Exception {
        Doctor isExist= doctorRepository.findByUserName(doctor.getUserName());
        if(isExist!=null){
            throw new Exception("Account already exists");
        }
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        Doctor savedDoctor=doctorRepository.save(doctor);
        Authentication authentication=new UsernamePasswordAuthenticationToken(savedDoctor.getUserName(),savedDoctor.getPassword());
        String token= JwtProvider.generateToken(authentication,doctor.getRole());
        AuthResponse res=new AuthResponse(token,"Register Success");

        return res;
    }
}
