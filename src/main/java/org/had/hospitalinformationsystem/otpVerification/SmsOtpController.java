package org.had.hospitalinformationsystem.otpVerification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/otp")
@Slf4j
public class SmsOtpController {

    @Autowired
    private SmsService smsService;

    @GetMapping("/process")
    public String processSMS() {
        return "SMS sent";
    }

    @PostMapping("/sendotp")
    public SmsOtpResponse sendOtp(@RequestBody SmsOtpRequest smsOtpRequest) {
        return smsService.sendSMS(smsOtpRequest);
    }
    @PostMapping("/validateotp")
    public String validateOtp(@RequestBody SmsOtpValidationRequest smsOtpValidationRequest) {
        return smsService.validateOtp(smsOtpValidationRequest);
    }
}