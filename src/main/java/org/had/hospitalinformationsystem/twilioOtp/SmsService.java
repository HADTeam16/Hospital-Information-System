package org.had.hospitalinformationsystem.twilioOtp;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmsService {

    @Autowired
    private TwilioConfig twilioConfig;
    Map<String, String> otpMap = new HashMap<>();


    public OtpResponse sendSMS(OtpRequest otpRequest) {
        OtpResponse otpResponse = null;
        try {
            PhoneNumber to = new PhoneNumber(otpRequest.getPhoneNumber());//to
            PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber()); // from
            String otp = generateOTP();
            String otpMessage = "Your One-Time Password (OTP) is " + otp + " This OTP ensures the security of your personal health data.\n" +
                    "By giving this OTP to receptionist, you consent to the sharing of your medical information among our healthcare professionals within our Hospital for comprehensive and personalized care." +
                    "\n" +
                    "Your privacy is our priority. If you did not request this OTP or have any concerns, please contact our support team immediately.\n" +
                    "\n" +
                    "Thank you for entrusting us with your health journey.";
            Message message = Message
                    .creator(to, from,
                            otpMessage)
                    .create();
            otpMap.put(otpRequest.getUsername(), otp);
            otpResponse = new OtpResponse(OtpStatus.DELIVERED, otpMessage);
        } catch (Exception e) {
            log.error("Error sending SMS: {}", e.getMessage(), e);
            otpResponse = new OtpResponse(OtpStatus.FAILED, e.getMessage());
        }
        return otpResponse;
    }

    public String validateOtp(OtpValidationRequest otpValidationRequest) {
        Set<String> keys = otpMap.keySet();
        String username = null;
        for(String key : keys)
            username = key;
        if (otpValidationRequest.getUsername().equals(username)) {
            otpMap.remove(username,otpValidationRequest.getOtpNumber());
            return "OTP is valid!";
        } else {
            return "OTP is invalid!";
        }
    }

    private String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }

}
