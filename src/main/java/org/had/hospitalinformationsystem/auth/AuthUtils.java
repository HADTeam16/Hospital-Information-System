package org.had.hospitalinformationsystem.auth;

import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.dto.*;
import org.had.hospitalinformationsystem.nurse.Nurse;
import org.had.hospitalinformationsystem.nurse.NurseRepository;
import org.had.hospitalinformationsystem.receptionist.Receptionist;
import org.had.hospitalinformationsystem.receptionist.ReceptionistRepository;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthUtils extends Utils{
    @Autowired
    private JavaMailSender sender;
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
    Utils utils = new Utils();

    Map<String, OtpInfo> otpMap = new HashMap<>();

    protected void handleException(Exception e) {
        if (e instanceof DataIntegrityViolationException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists with the same email. Try using a different email", e);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: " + e.getMessage(), e);
        }
    }

    protected User createUserWithAdminDetails() {
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
        user.setDateOfBirth("19021999");
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

    protected void saveUserAndDoctor(User newUser, Doctor newDoctor) {
        try {
            authRepository.save(newUser.getAuth());
            userRepository.save(newUser);
            doctorRepository.save(newDoctor);
        } catch (Exception e) {
            handleException(e);
        }
    }

    protected void saveUserAndReceptionist(User newUser, Receptionist newReceptionist) {
        try {
            authRepository.save(newUser.getAuth());
            userRepository.save(newUser);
            receptionistRepository.save(newReceptionist);
        } catch (Exception e) {
            handleException(e);
        }
    }

    protected void saveUserAndNurse(User newUser, Nurse newNurse) {
        try {
            authRepository.save(newUser.getAuth());
            userRepository.save(newUser);
            nurseRepository.save(newNurse);
        } catch (Exception e) {
            handleException(e);
        }
    }

    protected void sendEmailWithAccountDetails(String email, String username, String password, String name) {
        String subject = "Your account has been created successfully";
        String messageTemplate = "Hello Mr/Mrs "+name+",<br/><br/>" +
                "Your account has been created successfully. Happy to have you on board. Your LogIn Credentials are as below <br/>" +
                "<strong> Username:  </strong> " + username + "<br/>" +
                "<strong> Password: </strong> " + password + "<br/>" +
                "We request you to kindly login and change your Password. <br/>" +
                "Steps to follow: Login -> Go to your Profile -> Click on Change Password <br/>" +
                "Best regards,<br/>" +
                "Pure Zen Wellness Hospital";
        sendEmail(email, username, password, name, subject, messageTemplate);
    }

    protected void sendEmailWithNewPasswordDetails(String email, String username, String password, String name) {
        String subject = "New Log in Credentials";
        String messageTemplate = "Hello Mr/Mrs "+name+",<br/><br/>" +
                "As per your request, your password has been changed successfully.<br/> <br/>" +
                "<strong> Username:  </strong> " + username + "<br/>" +
                "<strong> Password: </strong> " + password + "<br/>" +
                "We request you to kindly login with your new credentials and change your Password. <br/>" +
                "Steps to follow: Login -> Go to your Profile -> Click on Change Password <br/>" +
                "Best regards,<br/>" +
                "Pure Zen Wellness Hospital";

        sendEmail(email, username, password, name, subject, messageTemplate);
    }

    protected void sendEmailWithAcknowledgementOfPasswordChange(String email, String username, String password, String name) {
        String subject = "Password Change Successfully";
        String messageTemplate = "Hello Mr/Mrs "+name+",<br/><br/>" +
                "This is a conformation that the password for your Pure zen wellness hospital account %s has just changed. <br/><br/>" +
                "If you didn't change your password, contact admin.<br/>" +
                "Best regards,<br/>" +
                "Pure Zen Wellness Hospital";

        sendEmail(email, username, password, name, subject, messageTemplate);
    }

    protected EmailOtpResponse sendEmailForForgetPassword(String email, String username, String name) {
        EmailOtpResponse otpResponse;

        try {
            String subject = "OTP for changing password";
            String otp = Utils.generateOTP();
            String message = "Dear " + name + ",<br/>" +
                    "The OTP to reset your Pure Zen Wellness Hospital account password is " + otp +
                    ". Valid for the next 10 minutes only.<br/>" +
                    "Best regards,<br/>" +
                    "Pure Zen Wellness Hospital";
            sendEmail(email, username, "", name, subject, message);
            Instant expirationTime = Instant.now().plusSeconds(600);
            otpMap.put(email, new OtpInfo(otp, expirationTime));
            otpResponse = new EmailOtpResponse(OtpStatus.DELIVERED, "Success");
        } catch (Exception e) {
            otpResponse = new EmailOtpResponse(OtpStatus.FAILED, e.getMessage());
        }
        return otpResponse;
    }

    protected ResponseEntity<Map<String,String>> validateOtp(String emailId,String otpNumber){
        OtpInfo otpInfo = otpMap.get(emailId);
        Map<String,String> response = new HashMap<>();
        if (otpInfo != null && otpInfo.getOtp().equals(otpNumber)) {
            if (Instant.now().isBefore(otpInfo.getExpirationTime())) {
                otpMap.remove(emailId);
                User user = userRepository.findUserByEmail(emailId);
                return generateAndSentNewPasswordToUser(user);
            } else {
                response.put("message","OTP has been expired");
                otpMap.remove(emailId);
                return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(response);
            }
        } else {
            response.put("message","Invalid OTP");
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(response);
        }
    }

    protected ResponseEntity<Map<String,String>> generateAndSentNewPasswordToUser(User user){
        Map<String,String> response = new HashMap<>();
        try {
            String newPassword = generateRandomString(10);
            String encodedNewPassword = Utils.hashPassword(newPassword, user.getAuth().getSalt());
            user.getAuth().setPassword(encodedNewPassword);
            try {
                sendEmailWithNewPasswordDetails(user.getEmail(), user.getUserName(), newPassword, user.getFirstName());
                response.put("message", "Password updated successfully");
                userRepository.save(user);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                response.put("message", "Failed to update password, Try again");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }
        catch (Exception e){
            response.put("message","Error!!!!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    protected Authentication authenticate(String userName, String password, String role) {
        User user = userRepository.findByUserName(userName);
        if(!Utils.verifyPassword(password,user.getAuth().getPassword(),user.getAuth().getSalt()) || !user.getRole().matches(role)){
            throw new BadCredentialsException("Invalid Username or password");
        }
        List<GrantedAuthority> authorities=new ArrayList<>();
        UserDetails userDetails= new org.springframework.security.core.userdetails.User(user.getUserName(),user.getAuth().getPassword(),authorities);
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }

}
