package org.had.hospitalinformationsystem.otpVerification;

import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.had.hospitalinformationsystem.dto.*;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class OtpVerificationServiceImpl extends OtpVerificationUtils implements OtpVerificationService{


    @Override
    public ResponseEntity<Map<String,String>> sendOtpViaMail(String jwt, EmailOtpRequest emailOtpRequest){
        Map<String, String> response = new HashMap<>();
        try{
            String role = JwtProvider.getRoleFromJwtToken(jwt);;
            if (role.equals("receptionist")) {
                sendEmailForConsent(emailOtpRequest.getEmail(), emailOtpRequest.getUsername(),emailOtpRequest.getName());
                response.put("message","OTP sent Successfully");
                return ResponseEntity.ok(response);
            }
            else{
                response.put("message","Access Denied!!!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }
        catch(Exception e){
            response.put("message","Error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    @Override
    public ResponseEntity<Map<String,String>> sendOtpViaSms(String jwt, SmsOtpRequest smsOtpRequest){
        Map<String, String> response = new HashMap<>();
        try{
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("receptionist")){

                sendSmsForConsent(smsOtpRequest.getPhoneNumber(),smsOtpRequest.getUsername());
                response.put("message","DELIVERED");
                return ResponseEntity.ok(response);
            }
            else{
                response.put("message","ACCESS DENIED");
                return ResponseEntity.ok(response);
            }
        }
        catch(Exception e){
            response.put("message","FAILED");
            return ResponseEntity.ok(response);
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> validateOtp(String jwt, OtpValidationRequest otpValidationRequest){
        Map<String, String> response = new HashMap<>();
        try{
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("receptionist")) {
                int val = validateOtp(otpValidationRequest);
                if(val==1){
                    response.put("message","OTP is valid");
                    return ResponseEntity.ok(response);
                }
                else if(val==2){
                    response.put("message","OTP has been expired");
                    return ResponseEntity.ok(response);
                }
                else{
                    response.put("message","Invalid OTP");
                    return ResponseEntity.ok(response);
                }
            }
            else {
                response.put("message", "Access denied!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Unknown error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
