package org.had.hospitalinformationsystem.otpVerification;

import java.text.DecimalFormat;
import java.time.Instant;
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
    Map<String, OtpInfo> otpMap = new HashMap<>();


    public SmsOtpResponse sendSMS(SmsOtpRequest smsOtpRequest) {
        SmsOtpResponse smsOtpResponse = null;
        try {
            PhoneNumber to = new PhoneNumber(smsOtpRequest.getPhoneNumber());//to
            PhoneNumber from = new PhoneNumber(smsTwilioConfig.getTrialNumber()); // from
            String otp = generateOTP();
            String otpMessage = "Your One-Time Password (OTP) is " + otp + " This OTP ensures the security of your personal health data. This OTP is valid for 10 minutes.\n" +
                    "By giving this OTP to receptionist, you consent to the sharing of your medical information among our healthcare professionals within our Hospital for comprehensive and personalized care." +
                    "\n" +
                    "Your privacy is our priority. If you did not request this OTP or have any concerns, please contact our support team immediately.\n" +
                    "\n" +
                    "Thank you for entrusting us with your health journey.";
            Message message = Message
                    .creator(to, from,
                            otpMessage)
                    .create();
            Instant expirationTime = Instant.now().plusSeconds(600);
            otpMap.put(smsOtpRequest.getUsername(), new OtpInfo(otp,expirationTime));
            smsOtpResponse = new SmsOtpResponse(OtpStatus.DELIVERED, otpMessage);
        } catch (Exception e) {
            smsOtpResponse = new SmsOtpResponse(OtpStatus.FAILED, e.getMessage());
        }
        return smsOtpResponse;
    }

    public String validateOtp(SmsOtpValidationRequest smsOtpValidationRequest) {
        String username = smsOtpValidationRequest.getUsername();
        OtpInfo smsOtpInfo = otpMap.get(username);
        if (smsOtpInfo != null && smsOtpInfo.getOtp().equals(smsOtpValidationRequest.getOtpNumber())) {
            if (Instant.now().isBefore(smsOtpInfo.getExpirationTime())) {
                otpMap.remove(username); // Remove OTP after successful validation
                return "OTP is valid!";
            } else {
                otpMap.remove(username); // Remove expired OTP entry from the map
                return "OTP has expired";
            }
        } else {
            return "OTP is invalid!";
        }
    }

    private String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }
}
