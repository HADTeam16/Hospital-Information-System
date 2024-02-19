package org.had.hospitalinformationsystem.otpVerification;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public EmailOtpResponse sendOtp(@RequestBody EmailOtpRequest emailOtpRequest) {
        log.info("inside sendOtp :: " + emailOtpRequest.getUsername());
        return emailOtpService.sendEmail(emailOtpRequest);
    }

    @PostMapping("/validateotp")
    public String validateOtp(@RequestBody EmailOtpValidationRequest emailOtpValidationRequest){
        log.info("inside validate :: " + emailOtpValidationRequest.getUsername() + " " + emailOtpValidationRequest.getEmailOtpNumber());
        return emailOtpService.validateOtp(emailOtpValidationRequest);
    }
}
