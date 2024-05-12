package org.had.hospitalinformationsystem.otpVerification;

import org.had.hospitalinformationsystem.dto.EmailOtpRequest;
import org.had.hospitalinformationsystem.dto.OtpValidationRequest;
import org.had.hospitalinformationsystem.dto.SmsOtpRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface OtpVerificationService {


    ResponseEntity<Map<String, String>> sendOtpViaMail(String jwt,EmailOtpRequest emailOtpRequest);

    ResponseEntity<Map<String,String>> sendOtpViaSms(String jwt, SmsOtpRequest smsOtpRequest);

    ResponseEntity<Map<String, String>> validateOtp(String jwt, OtpValidationRequest otpValidationRequest);

    ResponseEntity<Map<String,String>>sendOtpForConsentRemove(String jwt,EmailOtpRequest emailOtpRequest);

    ResponseEntity<Map<String, String>> validateOtpForConsentRemove(String jwt, OtpValidationRequest otpValidationRequest);
}
