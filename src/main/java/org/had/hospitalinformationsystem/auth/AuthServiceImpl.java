package org.had.hospitalinformationsystem.auth;

import jakarta.mail.internet.MimeMessage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.had.hospitalinformationsystem.otpVerification.*;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
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
    UserRepository userRepository;

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

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }

    public EmailOtpResponse sendEmailWithNewPassword(String username,String password,String email){
        EmailOtpResponse otpResponse = null;
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            String subject = "Recover your account";
            helper.setSubject(subject);
            String message = "Dear "+ username+",<br/>" +
                    "We request you to kindly login with your new credentials and change your Password. <br/>" +
                    "Steps to follow: Login -> Go to your Profile -> Click on Change Password <br/>" +
                    "<strong> Username: " + username + "</strong> <br/>" +
                    "<strong> Password: " + password + "</strong> <br/>" +
                    "Best regards,<br/>" +
                    "Pure Zen Wellness Hospital";

            helper.setText(message,true);
            sender.send(mimeMessage);
            otpResponse = new EmailOtpResponse(OtpStatus.DELIVERED,message);
        }
        catch(Exception e){
            otpResponse = new EmailOtpResponse(OtpStatus.FAILED,e.getMessage());
        }
        return otpResponse;
    }

    public ForgetPasswordEmailResponse validateOtp(EmailOtpValidationRequest emailOtpValidationRequest,String email){
        String username = emailOtpValidationRequest.getUsername();
        OtpInfo otpInfo = otpMap.get(username);
        ForgetPasswordEmailResponse response = new ForgetPasswordEmailResponse();
        if (otpInfo != null && otpInfo.getOtp().equals(emailOtpValidationRequest.getEmailOtpNumber())) {
            if (Instant.now().isBefore(otpInfo.getExpirationTime())) {
                otpMap.remove(username);

                String password = generateRandomString(10);
                EmailOtpResponse emailResponse = sendEmailWithNewPassword(username,password,email);

                if(emailResponse.getStatus().equals(OtpStatus.DELIVERED)){
                    response.setStatus("Email has been sent successfully with new credentials");
                    response.setPassword(password);
                }
                else{
                    response.setStatus("Failed to send email... Try Again....");
                    response.setPassword(null);
                    response.setIsSent(1);
                }
            } else {
                otpMap.remove(username);
                response.setStatus("OTP expired");
                response.setPassword(null);
                response.setIsSent(0);
            }
        } else {
            response.setStatus("Invalid OTP");
            response.setPassword(null);
            response.setIsSent(0);
        }
        return response;
    }


    private static String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }
}
