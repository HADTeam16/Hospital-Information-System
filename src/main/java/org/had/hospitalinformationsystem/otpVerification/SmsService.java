package org.had.hospitalinformationsystem.otpVerification;

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
    private SmsTwilioConfig smsTwilioConfig;
    Map<String, String> otpMap = new HashMap<>();


    public SmsOtpResponse sendSMS(SmsOtpRequest smsOtpRequest) {
        SmsOtpResponse smsOtpResponse = null;
        try {
            PhoneNumber to = new PhoneNumber(smsOtpRequest.getPhoneNumber());//to
            PhoneNumber from = new PhoneNumber(smsTwilioConfig.getTrialNumber()); // from
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
            otpMap.put(smsOtpRequest.getUsername(), otp);
            smsOtpResponse = new SmsOtpResponse(OtpStatus.DELIVERED, otpMessage);
        } catch (Exception e) {
            log.error("Error sending SMS: {}", e.getMessage(), e);
            smsOtpResponse = new SmsOtpResponse(OtpStatus.FAILED, e.getMessage());
        }
        return smsOtpResponse;
    }

    public String validateOtp(SmsOtpValidationRequest smsOtpValidationRequest) {
        Set<String> keys = otpMap.keySet();
        String username = null;
        for(String key : keys)
            username = key;
        if (smsOtpValidationRequest.getUsername().equals(username)) {
            otpMap.remove(username, smsOtpValidationRequest.getOtpNumber());
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
