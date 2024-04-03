package org.had.hospitalinformationsystem.otpVerification;


import lombok.extern.slf4j.Slf4j;
import org.had.hospitalinformationsystem.dto.AuthResponse;
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
    public ResponseEntity< String> processEmail(@RequestHeader("Authorization") String jwt) {
        try{
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("receptionist")) {
                return ResponseEntity.ok("Email Sent");
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
        }
    }


    @PostMapping("/send/otp")
    public ResponseEntity <EmailOtpResponse> sendOtp(@RequestHeader("Authorization") String jwt, @RequestBody EmailOtpRequest emailOtpRequest) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("receptionist")) {
                return ResponseEntity.ok(emailOtpService.sendEmail(emailOtpRequest));
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new EmailOtpResponse(OtpStatus.ACCESSDENIED,"Access Denied"));
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new EmailOtpResponse(OtpStatus.ERROR,e.getMessage()));
        }
    }

    @PostMapping("/validate/otp")
    public ResponseEntity <String> validateOtp(@RequestHeader("Authorization") String jwt, @RequestBody EmailOtpValidationRequest emailOtpValidationRequest){
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("receptionist")) {
                return ResponseEntity.ok(emailOtpService.validateOtp(emailOtpValidationRequest));
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
        }
    }
}
