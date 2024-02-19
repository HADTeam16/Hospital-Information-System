package org.had.hospitalinformationsystem.otpVerification;


import lombok.extern.slf4j.Slf4j;
import org.had.hospitalinformationsystem.dto.AuthResponse;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emailotp")
@Slf4j
public class EmailOtpController {

    @Autowired
    private EmailOtpService emailOtpService;


    @GetMapping("/process")
    public String processEmail() {
        return "Email Sent";
    }


    @PostMapping("/sendotp")
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

    @PostMapping("/validateotp")
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