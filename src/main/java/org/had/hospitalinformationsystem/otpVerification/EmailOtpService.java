package org.had.hospitalinformationsystem.otpVerification;



import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Service
@Slf4j
public class EmailOtpService {
    @Autowired
    private JavaMailSender sender;

    Map<String, OtpInfo> otpMap = new HashMap<>();

    public EmailOtpResponse sendEmailForConsent(EmailOtpRequest emailOtpRequest){
        EmailOtpResponse otpResponse = null;
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(emailOtpRequest.getEmail());
            String subject = "Verification for Pure Zen Wellness";
            helper.setSubject(subject);
            String otp = generateOTP();
            String message = "Dear User,<br/><br/>" +
                    "Welcome to Pure Zen Wellness Hospital! Your One-Time Password (OTP) is <strong>" + otp + "</strong>. This OTP ensures the security of your personal health data. This OTP is valid for 10 minutes.<br/><br/>" +
                    "By giving this OTP to the receptionist, you consent to the sharing of your medical information among our healthcare professionals within The Pure Zen Wellness Hospital for comprehensive and personalized care.<br/><br/>" +
                    "Your privacy is our priority. If you did not request this OTP or have any concerns, please contact our support team immediately.<br/><br/>" +
                    "Thank you for entrusting us with your health journey.<br/><br/>" +
                    "Best regards,<br/>" +
                    "Pure Zen Wellness Hospital";

            helper.setText(message,true);
            Instant expirationTime = Instant.now().plusSeconds(600);
            otpMap.put(emailOtpRequest.getUsername(),new OtpInfo(otp,expirationTime));
            sender.send(mimeMessage);

            otpResponse = new EmailOtpResponse(OtpStatus.DELIVERED,message);
        }
        catch(Exception e){
            otpResponse = new EmailOtpResponse(OtpStatus.FAILED,e.getMessage());
        }
        return otpResponse;
    }

    public String validateOtp(EmailOtpValidationRequest emailOtpValidationRequest){
        String username = emailOtpValidationRequest.getUsername();
        OtpInfo otpInfo = otpMap.get(username);

        if (otpInfo != null && otpInfo.getOtp().equals(emailOtpValidationRequest.getEmailOtpNumber())) {
            if (Instant.now().isBefore(otpInfo.getExpirationTime())) {
                otpMap.remove(username);
                return "OTP is valid";
            } else {
                otpMap.remove(username);
                return "OTP has expired";
            }
        } else {
            return "OTP is invalid";
        }
    }

    private static String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }
}
