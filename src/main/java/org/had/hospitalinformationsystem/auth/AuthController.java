package org.had.hospitalinformationsystem.auth;

import org.had.hospitalinformationsystem.dto.AuthResponse;
import org.had.hospitalinformationsystem.dto.ChangePasswordRequest;
import org.had.hospitalinformationsystem.dto.LoginRequest;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @GetMapping("/signup/admin")
    public ResponseEntity<AuthResponse> createAdmin() {
        return authService.createAdmin();
    }

    @PostMapping("/signup/user")
    public ResponseEntity<Object> createUser(@RequestHeader("Authorization") String jwt, @RequestBody RegistrationDto registrationDto) {
        return authService.createUser(jwt,registrationDto);
    }

    @PostMapping("/signin")
    public ResponseEntity< AuthResponse>signIn(@RequestBody LoginRequest loginRequest) {
        return authService.signIn(loginRequest);
    }

    @PutMapping("/admin/change/password/{id}")
    public ResponseEntity<Map<String,String>> changePasswordByAdmin(@RequestHeader("Authorization") String jwt, @PathVariable Long id) {
        return authService.changePasswordByAdmin(jwt,id);
    }

    @PutMapping("/user/change/password")
    public ResponseEntity< Map<String,String>> changePasswordByUser(@RequestHeader("Authorization") String jwt,@RequestBody ChangePasswordRequest changePasswordRequest) {
        return authService.changePasswordByUser(jwt,changePasswordRequest);
    }

    @PostMapping("/user/forget/password/send/otp/{emailId}")
    public ResponseEntity<?> sendOtpForForgetPasswordByUser(@PathVariable String emailId){
        return authService.sendOtpForForgetPasswordByUser(emailId);
    }

    @PostMapping("/user/forget/password/validate/otp/{emailId}/{otp}")
    public ResponseEntity<?> validateOtpForForgetPasswordByUser(@PathVariable String emailId, @PathVariable String otp){
        return authService.validateOtpForForgetPasswordByUser(emailId,otp);
    }

    @PutMapping("/toggle/user/status/{userId}")
    public ResponseEntity<?> toggleUserLogInStatus(@RequestHeader("Authorization") String jwt, @PathVariable Long userId){
        return authService.toggleUserLogInStatus(jwt,userId);
    }
}