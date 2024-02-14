package org.had.hospitalinformationsystem.auth;

import org.had.hospitalinformationsystem.dto.AuthResponse;
import org.had.hospitalinformationsystem.dto.ChangePasswordRequest;
import org.had.hospitalinformationsystem.dto.LoginRequest;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.nurse.Nurse;
import org.had.hospitalinformationsystem.nurse.NurseRepository;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.receptionist.Receptionist;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.receptionist.ReceptionistRepository;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CustomerUserDetailsServiceImplementation customerUserDetailsService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DoctorRepository doctorRepository;
//    @Autowired
//    PatientRepository patientRepository;
    @Autowired
    ReceptionistRepository receptionistRepository;
    @Autowired
    NurseRepository nurseRepository;

    @Autowired
    Utils utils = new Utils();

    //Add Admin
    @GetMapping("/signup/admin")
    public AuthResponse createAdmin(){
        User user=new User();
        user.setUserName("admin");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setRole("admin");
        user.setEmail("admin@gmail.com");
        userRepository.save(user);
        Authentication authentication=new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        String token= JwtProvider.generateToken(authentication,user.getRole());
        return new AuthResponse(token,"Register Success", user);
    }


    // Add Users except Patient
    @PostMapping("/signup/user")
    public AuthResponse createUser(@RequestHeader("Authorization") String jwt,@RequestBody RegistrationDto registrationDto){

        User newUser = utils.getUser(registrationDto);
        User savedUser = new User();

        String role = JwtProvider.getRoleFromJwtToken(jwt);

        if (role.equals("admin") && registrationDto.getRole().equals("doctor")) {
            savedUser = userRepository.save(newUser);
            Doctor newDoctor = new Doctor();
            newDoctor.setUser(savedUser);
            newDoctor.setSpecialization(registrationDto.getSpecialization());
            doctorRepository.save(newDoctor);
        }
        else if (role.equals("admin") && registrationDto.getRole().equals("receptionist")) {
            savedUser = userRepository.save(newUser);
            Receptionist newReceptionist  =  new Receptionist();
            newReceptionist.setUser(savedUser);
            receptionistRepository.save(newReceptionist);
        }
        else if (role.equals("admin") && registrationDto.getRole().equals("nurse")) {
            savedUser = userRepository.save(newUser);
            Nurse newNurse = new Nurse();
            newNurse.setUser(savedUser);
            newNurse.setHeadNurse(registrationDto.isHeadNurse());

            nurseRepository.save(newNurse);
        }

        Authentication authentication=new UsernamePasswordAuthenticationToken(savedUser.getUserName(),savedUser.getPassword());
        String token= JwtProvider.generateToken(authentication,newUser.getRole());
        return new AuthResponse(token,"Register Success",savedUser);
    }


    // Authenticate
    private Authentication authenticate(String userName, String password,String role) {
        UserDetails userDetails=customerUserDetailsService.loadUserByUsername(userName);
        if(userDetails==null){
            throw new BadCredentialsException("invalid username");
        }
        if(!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new BadCredentialsException("password not matched");
        }
        User user=userRepository.findByUserName(userDetails.getUsername());
        if(!user.getRole().matches(role)){
            throw new BadCredentialsException("role not matched");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }

    // User Sign In
    @PostMapping("/signin")
    public AuthResponse signIn(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticate(loginRequest.getUserName(), loginRequest.getPassword(), loginRequest.getRole());
        String token = JwtProvider.generateToken(authentication, loginRequest.getRole());
        String userName = JwtProvider.getUserNameFromJwtTokenUnfiltered(token);
        User user = userRepository.findByUserName(userName);
        return new AuthResponse(token, "Login Success",user);
    }


    @PutMapping("/change/password")
    public String changePassword(@RequestHeader("Authorization") String jwt,@RequestBody ChangePasswordRequest changePasswordRequest) {

        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();

        String userName = JwtProvider.getUserNameFromJwtToken(jwt);
        User currUser = userRepository.findByUserName(userName);

        if (passwordEncoder.matches(oldPassword, currUser.getPassword())) {
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            currUser.setPassword(encodedNewPassword);
            userRepository.save(currUser);

            return "Password updated successfully!";
        } else {
            return "Incorrect old password. Password not updated.";
        }
    }

}
