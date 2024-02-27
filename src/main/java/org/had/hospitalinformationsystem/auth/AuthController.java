package org.had.hospitalinformationsystem.auth;

import org.had.hospitalinformationsystem.dto.AuthResponse;
import org.had.hospitalinformationsystem.dto.ChangePasswordRequest;
import org.had.hospitalinformationsystem.dto.LoginRequest;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.nurse.Nurse;
import org.had.hospitalinformationsystem.nurse.NurseRepository;
import org.had.hospitalinformationsystem.receptionist.Receptionist;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.receptionist.ReceptionistRepository;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    CustomerUserDetailsServiceImplementation customerUserDetailsService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    ReceptionistRepository receptionistRepository;
    @Autowired
    NurseRepository nurseRepository;

    @Autowired
    Utils utils = new Utils();

    //Add Admin
    @PostMapping("/signup/admin")
    public ResponseEntity <AuthResponse> createAdmin(){

        if (userRepository.findAdminByRole()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponse(null, "Admin already exist, Can not add another admin", null));
        }
        try {
            User user = new User();
            user.setUserName("admin");
            String salt = user.getUserName() + "gfdsdfedfvfsJKJHGKJBBNK";
            user.setSalt(salt);
            user.setPassword(Utils.hashPassword("1234",salt));
            user.setRole("admin");
            user.setEmail("admin@gmail.com");
            userRepository.save(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
            String token = JwtProvider.generateToken(authentication, user.getRole());
            return ResponseEntity.ok(new AuthResponse(token, "Register Success", user));
        }
        catch(AuthenticationException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, "Error adding Admin", null));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, "Error", null));
        }
    }


    // Add Users except Patient
    @PostMapping("/signup/user")
    public ResponseEntity< AuthResponse> createUser(@RequestHeader("Authorization") String jwt,@RequestBody RegistrationDto registrationDto){
        try {
            User newUser = utils.getUser(registrationDto);
            User savedUser = new User();
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("admin")){
                switch (registrationDto.getRole()) {
                    case "doctor" -> {
                        savedUser = userRepository.save(newUser);
                        Doctor newDoctor = new Doctor();
                        newDoctor.setUser(savedUser);
                        newDoctor.setSpecialization(registrationDto.getSpecialization());
                        newDoctor.setWorkStart(registrationDto.getWorkStart());
                        newDoctor.setWorkEnd(registrationDto.getWorkEnd());
                        doctorRepository.save(newDoctor);
                    }
                    case "receptionist" -> {
                        savedUser = userRepository.save(newUser);
                        Receptionist newReceptionist = new Receptionist();
                        newReceptionist.setUser(savedUser);
                        receptionistRepository.save(newReceptionist);
                    }
                    case "nurse" -> {
                        savedUser = userRepository.save(newUser);
                        Nurse newNurse = new Nurse();
                        newNurse.setUser(savedUser);
                        newNurse.setHeadNurse(registrationDto.isHeadNurse());

                        nurseRepository.save(newNurse);
                    }
                    default -> {
                        return ResponseEntity.ok( new AuthResponse("", "Access Denied", savedUser));
                    }
                }
                Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getUserName(), savedUser.getPassword());
                String token = JwtProvider.generateToken(authentication, newUser.getRole());
                return ResponseEntity.ok( new AuthResponse(token, "Register Success", savedUser));
            }
            else {
                return ResponseEntity.ok(new AuthResponse("", "Access Denied", savedUser));
            }
        }
        catch(BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null,"Invalid Token",null));
        }
        catch (AuthenticationException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null,"Error adding User",null));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null,"Error",null));
        }
    }


    // Authenticate
    private Authentication authenticate(String userName, String password,String role) {
        UserDetails userDetails=customerUserDetailsService.loadUserByUsername(userName);
        User currUser = userRepository.findByUserName(userName);
        if(userDetails==null){
            throw new BadCredentialsException("Invalid Username or password");
        }

        String salt = currUser.getSalt();
        if(!Utils.verifyPassword(password,userDetails.getPassword(),salt)){
            throw new BadCredentialsException("Invalid Username or password");
        }
        User user=userRepository.findByUserName(userDetails.getUsername());
        if(!user.getRole().matches(role)){
            throw new BadCredentialsException("Invalid Username or password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }

    // User Sign In
    @PostMapping("/signin")
    public ResponseEntity< AuthResponse>signIn(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticate(loginRequest.getUserName(), loginRequest.getPassword(), loginRequest.getRole());
            String token = JwtProvider.generateToken(authentication, loginRequest.getRole());
            String userName = JwtProvider.getUserNameFromJwtTokenUnfiltered(token);
            User user = userRepository.findByUserName(userName);
            return ResponseEntity.ok(new AuthResponse(token, "Login Success", user));
        }
        catch(AuthenticationException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, e.getMessage(), null));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, "Error", null));
        }
    }

    @PutMapping("/admin/changepassword")
    public ResponseEntity< String> changePasswordByAdmin(@RequestHeader("Authorization") String jwt,@RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("admin")) {
                User user = userRepository.findByUserName(changePasswordRequest.getUserName());
                String encodedNewPassword = Utils.hashPassword(changePasswordRequest.getNewPassword(),user.getSalt());
                user.setPassword(encodedNewPassword);
                userRepository.save(user);
                return ResponseEntity.ok("Password updated successfully");
            } else {
                return ResponseEntity.ok("Permission Denied");
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error");
        }
    }

    @PutMapping("/user/changepassword")
    public ResponseEntity< String> changePassword(@RequestHeader("Authorization") String jwt,@RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            String oldPassword = changePasswordRequest.getOldPassword();
            String newPassword = changePasswordRequest.getNewPassword();

            String userName = JwtProvider.getUserNameFromJwtToken(jwt);
            User currUser = userRepository.findByUserName(userName);

            if (Utils.verifyPassword(oldPassword, currUser.getPassword(),currUser.getSalt())) {
                String encodedNewPassword = Utils.hashPassword(newPassword,currUser.getSalt());
                currUser.setPassword(encodedNewPassword);
                userRepository.save(currUser);
                return ResponseEntity.ok("Password updated successfully");
            } else {
                return ResponseEntity.ok("Check your current password");
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error");
        }
    }
}