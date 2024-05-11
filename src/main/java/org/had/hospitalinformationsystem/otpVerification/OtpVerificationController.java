package org.had.hospitalinformationsystem.otpVerification;

import lombok.extern.slf4j.Slf4j;
import org.had.hospitalinformationsystem.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/otp/verification")
@Slf4j
public class OtpVerificationController {

    @Autowired
    OtpVerificationService otpVerificationService;

    @PostMapping("/send/otp/via/email")
    public ResponseEntity<Map<String,String>>sendOtp(@RequestHeader("Authorization") String jwt, @RequestBody EmailOtpRequest emailOtpRequest){
        return otpVerificationService.sendOtpViaMail(jwt,emailOtpRequest);
    }

    @PostMapping("/send/otp/via/sms")
    public ResponseEntity<Map<String,String>> sendOtp(@RequestHeader("Authorization") String jwt, @RequestBody SmsOtpRequest smsOtpRequest) {
        return otpVerificationService.sendOtpViaSms(jwt,smsOtpRequest);
    }

    @PostMapping("/validate/otp")
    public ResponseEntity<Map<String, String>> validateOtp(@RequestHeader("Authorization") String jwt,@RequestBody OtpValidationRequest otpValidationRequest){
        System.out.println(otpValidationRequest.getUsername());
        return otpVerificationService.validateOtp(jwt, otpValidationRequest);
    }

    @PostMapping("/send/otp/via/email/fot/consent/remove")
    public ResponseEntity<Map<String,String>>sendOtpForConsentRemove(@RequestHeader("Authorization") String jwt, @RequestBody EmailOtpRequest emailOtpRequest){
        return otpVerificationService.sendOtpForConsentRemove(jwt,emailOtpRequest);
    }

    @PostMapping("/validate/otp/for/consent/remove")
    public ResponseEntity<Map<String, String>> validateOtpForConsentRemove(@RequestHeader("Authorization") String jwt,@RequestBody OtpValidationRequest otpValidationRequest){
        return otpVerificationService.validateOtpForConsentRemove(jwt, otpValidationRequest);
    }

}
