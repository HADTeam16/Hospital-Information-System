package org.had.hospitalinformationsystem.auth;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.nurse.Nurse;
import org.had.hospitalinformationsystem.nurse.NurseRepository;
import org.had.hospitalinformationsystem.otpVerification.*;
import org.had.hospitalinformationsystem.receptionist.Receptionist;
import org.had.hospitalinformationsystem.receptionist.ReceptionistRepository;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;


@Service
@Slf4j
public class AuthServiceImpl implements AuthService{

    Map<String, OtpInfo> otpMap = new HashMap<>();

    @Autowired
    UserRepository userRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    ReceptionistRepository receptionistRepository;
    @Autowired
    NurseRepository nurseRepository;
    @Autowired
    AuthRepository authRepository;

    @Autowired
    private JavaMailSender sender;

    private static String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }

    private void sendEmail(String email, String username, String password, String name, String subject, String messageTemplate) {
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(subject);

            String message = String.format(messageTemplate, name, username, password);

            helper.setText(message, true);
            sender.send(mimeMessage);
        } catch (Exception ignored) {
        }
    }

    private void handleException(Exception e) {
        if (e instanceof DataIntegrityViolationException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists with the same email. Try using a different email", e);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: " + e.getMessage(), e);
        }
    }

    private static String generateRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder(10);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            randomString.append(randomChar);
        }
        return randomString.toString();
    }

    private EmailOtpResponse sendEmailWithNewPassword(String username,String password,String email){
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

    public User createUserWithAdminDetails() {
        User user = new User();
        Auth auth = new Auth();
        user.setUserName("admin");
        user.setDisable(false);
        String salt = user.getUserName() + "gfdsdfedfvfsJKJHGKJBBNK";
        auth.setSalt(salt);
        auth.setPassword(Utils.hashPassword("1234", salt));
        user.setAuth(auth);
        user.setRole("admin");
        user.setEmail("admin@gmail.com");
        user.setAddressLine1("address");
        user.setCity("Ecity");
        user.setContact("7418529638");
        user.setCountry("India");
        user.setDateOfBirth("cvcbnxgfhd");
        user.setEmail("email");
        user.setEmergencyContactNumber("7418529639");
        user.setEmergencyContactName("HAD");
        user.setFirstName("HAD");
        user.setGender("male");
        user.setLandmark("Ecity");
        user.setPinCode("560100");
        user.setState("Karnataka");
        return user;
    }

    public void saveUserAndDoctor(User newUser, Doctor newDoctor) {
        try {
            authRepository.save(newUser.getAuth());
            userRepository.save(newUser);
            doctorRepository.save(newDoctor);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void saveUserAndReceptionist(User newUser, Receptionist newReceptionist) {
        try {
            authRepository.save(newUser.getAuth());
            userRepository.save(newUser);
            receptionistRepository.save(newReceptionist);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void saveUserAndNurse(User newUser, Nurse newNurse) {
        try {
            authRepository.save(newUser.getAuth());
            userRepository.save(newUser);
            nurseRepository.save(newNurse);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void sendEmailWithAccountDetails(String email, String username, String password, String name) {
        String subject = "Your account has been created successfully";
        String messageTemplate = "Hello Mr/Mrs "+name+",<br/><br/>" +
                "Your account has been created successfully. Happy to have you on board. Your LogIn Credentials are as below <br/>" +
                "<strong> Username: %s </strong> <br/>" +
                "<strong> Password: %s </strong> <br/>" +
                "We request you to kindly login and change your Password. <br/>" +
                "Steps to follow: Login -> Go to your Profile -> Click on Change Password <br/>" +
                "Best regards,<br/>" +
                "Pure Zen Wellness Hospital";
        sendEmail(email, username, password, name, subject, messageTemplate);
    }

    @Override
    public void sendEmailWithNewPasswordDetails(String email, String username, String password, String name) {
        String subject = "New Log in Credentials";
        String messageTemplate = "Hello Mr/Mrs "+name+",<br/><br/>" +
                "As per your request, your password has been changed successfully.<br/> <br/>" +
                "<strong> Username: %s </strong> <br/>" +
                "<strong> Password: %s </strong> <br/><br/>" +
                "We request you to kindly login with your new credentials and change your Password. <br/>" +
                "Steps to follow: Login -> Go to your Profile -> Click on Change Password <br/>" +
                "Best regards,<br/>" +
                "Pure Zen Wellness Hospital";

        sendEmail(email, username, password, name, subject, messageTemplate);
    }

    @Override
    public void sendEmailWithAcknowledgementOfPasswordChange(String email, String username, String password, String name) {
        String subject = "Password Change Successfully";
        String messageTemplate = "Hello Mr/Mrs "+name+",<br/><br/>" +
                "This is a conformation that the password for your Pure zen wellness hospital account %s has just changed. <br/><br/>" +
                "If you didn't change your password, contact admin.<br/>" +
                "Best regards,<br/>" +
                "Pure Zen Wellness Hospital";

        sendEmail(email, username, password, name, subject, messageTemplate);
    }

    @Override
    public EmailOtpResponse sendEmailForForgetPassword(String email, String username, String password, String name) {
        EmailOtpResponse otpResponse;
        try {
            String subject = "OTP for changing password";
            String otp = generateOTP();
            String message = "Dear " + name + ",<br/>" +
                    "The OTP to reset your Pure Zen Wellness Hospital account password is " + otp +
                    ". Valid for the next 10 minutes only.<br/>" +
                    "Best regards,<br/>" +
                    "Pure Zen Wellness Hospital";

            sendEmail(email, username, password, name, subject, message);

            Instant expirationTime = Instant.now().plusSeconds(600);
            otpMap.put(username, new OtpInfo(otp, expirationTime));

            otpResponse = new EmailOtpResponse(OtpStatus.DELIVERED, message);
        } catch (Exception e) {
            otpResponse = new EmailOtpResponse(OtpStatus.FAILED, e.getMessage());
        }
        return otpResponse;
    }

    @Override
    public ForgetPasswordEmailResponse validateOtp(EmailOtpValidationRequest emailOtpValidationRequest,String email){
        String username = emailOtpValidationRequest.getUsername();
        OtpInfo otpInfo = otpMap.get(username);
        ForgetPasswordEmailResponse response = new ForgetPasswordEmailResponse();
        if (otpInfo != null && otpInfo.getOtp().equals(emailOtpValidationRequest.getEmailOtpNumber())) {
            if (Instant.now().isBefore(otpInfo.getExpirationTime())) {
                otpMap.remove(username);

                String password = generateRandomString();
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

    @Override
    public Authentication authenticate(String userName, String password, String role) {
        User user = userRepository.findByUserName(userName);
        if(!Utils.verifyPassword(password,user.getAuth().getPassword(),user.getAuth().getSalt()) || !user.getRole().matches(role)){
            throw new BadCredentialsException("Invalid Username or password");
        }
        List<GrantedAuthority> authorities=new ArrayList<>();
        UserDetails userDetails= new org.springframework.security.core.userdetails.User(user.getUserName(),user.getAuth().getPassword(),authorities);
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }
}
