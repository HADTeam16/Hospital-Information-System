package org.had.hospitalinformationsystem.auth;

import jakarta.mail.internet.MimeMessage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.had.hospitalinformationsystem.otpVerification.EmailOtpResponse;
import org.had.hospitalinformationsystem.otpVerification.EmailOtpValidationRequest;
import org.had.hospitalinformationsystem.otpVerification.OtpInfo;
import org.had.hospitalinformationsystem.otpVerification.OtpStatus;
import org.had.hospitalinformationsystem.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@Service
@Slf4j
public class AuthServiceImpl implements AuthService{

    Map<String, OtpInfo> otpMap = new HashMap<>();

    @Autowired
    private JavaMailSender sender;

    @Override
    public void sendEmailWithAccountDetails(String email, String username, String password, String name){
        try {

            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            String subject = "Your account has been created successfully";
            helper.setSubject(subject);
            String message = "Hello Mr/Mrs "+ name +",<br/><br/>" +
                    "Your account has been created successfully. Happy to have you on board. Your LogIn Credentials are as below <br/>" +
                    "<strong> Username: " + username + "</strong> <br/>" +
                    "<strong> Password: " + password + "</strong> <br/>" +
                    "We request you to kindly login and change your Password. <br/>" +
                    "Steps to follow: Login -> Go to your Profile -> Click on Change Password <br/>" +
                    "Best regards,<br/>" +
                    "Pure Zen Wellness Hospital";

            helper.setText(message,true);
            sender.send(mimeMessage);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void sendEmailWithNewPasswordDetails(String email, String username, String password, String name){
        try {

            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            String subject = "New Log in Credentials";
            helper.setSubject(subject);
            String message = "Hello Mr/Mrs "+ name +",<br/><br/>" +
                    "As per your request, your password has been changed successfully.<br/> <br/>" +
                    "<strong> Username: " + username + "</strong> <br/>" +
                    "<strong> Password: " + password + "</strong> <br/><br/>" +
                    "We request you to kindly login with your new credentials and change your Password. <br/>" +
                    "Steps to follow: Login -> Go to your Profile -> Click on Change Password <br/>" +
                    "Best regards,<br/>" +
                    "Pure Zen Wellness Hospital";

            helper.setText(message,true);
            sender.send(mimeMessage);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void sendEmailWithAcknowledgementOfPasswordChange(String email, String username, String password, String name){
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            String subject = "Password Change Successfully";
            helper.setSubject(subject);
            String message = "Hello Mr/Mrs "+ name +",<br/><br/>" +
                    "This is a conformation that the password for your Pure zen wellness hospital account "+ username +" has just changed. <br/><br/>"+
                    "If you didn't change your password, contact admin.<br/>" +
                    "Best regards,<br/>" +
                    "Pure Zen Wellness Hospital";

            helper.setText(message,true);
            sender.send(mimeMessage);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public EmailOtpResponse sendEmailForForgetPassword(User user){
        EmailOtpResponse otpResponse = null;
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(user.getEmail());
            String subject = "OTP for changing password";
            helper.setSubject(subject);
            String otp = generateOTP();
            String message = "Dear "+ user.getUserName()+",<br/>" +
                    "The OTP to reset your Pure Zen Wellness Hospital account password is "+ otp +
                    "Valid for next 10 minutes only"+
                    "Best regards,<br/>" +
                    "Pure Zen Wellness Hospital";

            helper.setText(message,true);
            Instant expirationTime = Instant.now().plusSeconds(600);
            otpMap.put(user.getUserName(),new OtpInfo(otp,expirationTime));
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
