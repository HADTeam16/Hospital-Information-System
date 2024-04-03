package org.had.hospitalinformationsystem.otpVerification;

import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sms/otp")
@Slf4j
public class SmsOtpController {

    @Autowired
    private SmsService smsService;

    @GetMapping("/process")
    public String processSMS() {
        return "SMS sent";
    }

    @PostMapping("/send/otp")
    public ResponseEntity<SmsOtpResponse> sendOtp(@RequestHeader("Authorization") String jwt,@RequestBody SmsOtpRequest smsOtpRequest) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("receptionist")) {
                return ResponseEntity.ok(smsService.sendSMS(smsOtpRequest));
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new SmsOtpResponse(OtpStatus.ACCESSDENIED,"Access Denied"));
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new SmsOtpResponse(OtpStatus.ERROR,e.getMessage()));
        }
    }
    @PostMapping("/validate/otp")
    public ResponseEntity< String> validateOtp(@RequestHeader("Authorization") String jwt,@RequestBody SmsOtpValidationRequest smsOtpValidationRequest) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("receptionist")) {
                return ResponseEntity.ok(smsService.validateOtp(smsOtpValidationRequest));
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR");
        }
    }
}