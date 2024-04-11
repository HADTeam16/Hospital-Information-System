package org.had.hospitalinformationsystem.otpVerification;

import com.twilio.type.PhoneNumber;
import org.had.hospitalinformationsystem.dto.OtpValidationRequest;
import org.had.hospitalinformationsystem.dto.OtpInfo;
import org.had.hospitalinformationsystem.dto.SmsTwilioConfig;
import org.had.hospitalinformationsystem.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class OtpVerificationUtils extends Utils {

    @Autowired
    private JavaMailSender sender;
    @Autowired
    private SmsTwilioConfig smsTwilioConfig;

    Map<String, OtpInfo> otpMap = new HashMap<>();

    protected void sendEmailForConsent(String email, String username, String name) {
        String otp = generateOTP();
        String subject = "Verification for Pure Zen Wellness";
        String messageTemplate = "Dear User,<br/><br/>" +
                "Welcome to Pure Zen Wellness Hospital! Your One-Time Password (OTP) is <strong>" + otp + "</strong>. This OTP ensures the security of your personal health data. This OTP is valid for 10 minutes.<br/><br/>" +
                "By giving this OTP to the receptionist, you consent to the sharing of your medical information among our healthcare professionals within The Pure Zen Wellness Hospital for comprehensive and personalized care.<br/><br/>" +
                "Your privacy is our priority. If you did not request this OTP or have any concerns, please contact our support team immediately.<br/><br/>" +
                "Thank you for entrusting us with your health journey.<br/><br/>" +
                "Best regards,<br/>" +
                "Pure Zen Wellness Hospital";
        sendEmail(email, username, "", name, subject, messageTemplate);
        Instant expirationTime = Instant.now().plusSeconds(600);
        otpMap.put(email,new OtpInfo(otp,expirationTime));
    }

    protected  void sendSmsForConsent(String phoneNumber,String name){
        String otp = generateOTP();
        String otpMessage = "Your One-Time Password (OTP) is " + otp + " This OTP ensures the security of your personal health data. This OTP is valid for 10 minutes.\n" +
                "By giving this OTP to receptionist, you consent to the sharing of your medical information among our healthcare professionals within our Hospital for comprehensive and personalized care." +
                "\n" +
                "Your privacy is our priority. If you did not request this OTP or have any concerns, please contact our support team immediately.\n" +
                "\n" +
                "Thank you for entrusting us with your health journey.";
        PhoneNumber to = new PhoneNumber(phoneNumber);
        sendSms(to,otpMessage);
        Instant expirationTime = Instant.now().plusSeconds(600);
        otpMap.put(phoneNumber, new OtpInfo(otp,expirationTime));
    }

    protected int validateOtp(OtpValidationRequest otpValidationRequest){
        String username = otpValidationRequest.getUsername();
        OtpInfo otpInfo = otpMap.get(username);

        if (otpInfo != null && otpInfo.getOtp().equals(otpValidationRequest.getOtpNumber())) {
            if (Instant.now().isBefore(otpInfo.getExpirationTime())) {
                otpMap.remove(username);
                return 1;
            } else {
                otpMap.remove(username);
                return 0;
            }
        } else {
            return -1;
        }
    }




}
