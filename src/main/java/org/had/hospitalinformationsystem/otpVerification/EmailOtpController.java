package org.had.hospitalinformationsystem.otpVerification;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email/otp")
@Slf4j
public class EmailOtpController {

    @Autowired
    private EmailOtpService emailOtpService;

    @GetMapping("/process")
    public ResponseEntity<String> processEmail(@RequestHeader("Authorization") String jwt) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("receptionist")) {
                return ResponseEntity.ok("Email Sent");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
        }
    }

    @PostMapping("/send/otp")
    public ResponseEntity<EmailOtpResponse> sendOtp(@RequestHeader("Authorization") String jwt,
            @RequestBody EmailOtpRequest emailOtpRequest) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("receptionist")) {
                return ResponseEntity.ok(emailOtpService.sendEmailForConsent(emailOtpRequest));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new EmailOtpResponse(OtpStatus.ACCESSDENIED, "Access Denied"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new EmailOtpResponse(OtpStatus.ERROR, e.getMessage()));
        }
    }

    @PostMapping("/validate/otp")
    public ResponseEntity<Map<String, String>> validateOtp(@RequestHeader("Authorization") String jwt,
            @RequestBody EmailOtpValidationRequest emailOtpValidationRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("receptionist")) {
                String validation = emailOtpService.validateOtp(emailOtpValidationRequest);
                response.put("message", validation);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Access denied!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Unknown error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
