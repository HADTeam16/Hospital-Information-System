package org.had.hospitalinformationsystem.otpVerification;



import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Service
@Slf4j
public class EmailOtpService {
    @Autowired
    private JavaMailSender sender;

    Map<String, String> otpMap = new HashMap<>();

    public EmailOtpResponse sendEmail(EmailOtpRequest emailOtpRequest){
        EmailOtpResponse otpResponse = null;
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(emailOtpRequest.getEmail());
            String subject = "Verification for Pure Zen Wellness";
            helper.setSubject(subject);
            String otp = generateOTP();
            String message = "Dear User,<br/><br/>" +
                    "Welcome to Pure Zen Wellness Hospital! Your One-Time Password (OTP) is <strong>" + otp + "</strong>. This OTP ensures the security of your personal health data.<br/><br/>" +
                    "By giving this OTP to the receptionist, you consent to the sharing of your medical information among our healthcare professionals within The Pure Zen Wellness Hospital for comprehensive and personalized care.<br/><br/>" +
                    "Your privacy is our priority. If you did not request this OTP or have any concerns, please contact our support team immediately.<br/><br/>" +
                    "Thank you for entrusting us with your health journey.<br/><br/>" +
                    "Best regards,<br/>" +
                    "Pure Zen Wellness Hospital";

            helper.setText(message,true);
            otpMap.put(emailOtpRequest.getUsername(),otp);
            sender.send(mimeMessage);

            otpResponse = new EmailOtpResponse(OtpStatus.DELIVERED,message);
        }
        catch(Exception e){
            otpResponse = new EmailOtpResponse(OtpStatus.FAILED,e.getMessage());
        }
        return otpResponse;
    }

    public String validateOtp(EmailOtpValidationRequest emailOtpValidationRequest){
        Set<String> keys = otpMap.keySet();
        String username = null;
        for(String key : keys)
            username = key;
        if(emailOtpValidationRequest.getUsername().equals(username)){
            otpMap.remove(username,emailOtpValidationRequest.getEmailOtpNumber());
            return  "OTP is valid";
        }
        else{
            return "OTP is invalid";
        }
    }

    private String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }
}
