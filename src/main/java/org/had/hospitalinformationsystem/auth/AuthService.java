package org.had.hospitalinformationsystem.auth;

import org.had.hospitalinformationsystem.dto.AuthResponse;
import org.had.hospitalinformationsystem.dto.ChangePasswordRequest;
import org.had.hospitalinformationsystem.dto.LoginRequest;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<AuthResponse> createAdmin();

    ResponseEntity<Object> createUser(String jwt, RegistrationDto registrationDto);

    ResponseEntity< AuthResponse>signIn(LoginRequest loginRequest);

    ResponseEntity< String> changePasswordByAdmin(String jwt, ChangePasswordRequest changePasswordRequest);

    ResponseEntity< String> changePasswordByUser(String jwt, ChangePasswordRequest changePasswordRequest);

    ResponseEntity<?> sendOtpForForgetPasswordByUser(String emailId);

    ResponseEntity<?> validateOtpForForgetPasswordByUser(String emailId, String otp);

    ResponseEntity<?> toggleUserLogInStatus(String jwt, Long userId);
}
