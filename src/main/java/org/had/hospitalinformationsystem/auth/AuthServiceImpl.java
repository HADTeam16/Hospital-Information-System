package org.had.hospitalinformationsystem.auth;

import lombok.extern.slf4j.Slf4j;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.dto.*;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.nurse.Nurse;
import org.had.hospitalinformationsystem.otpVerification.OtpVerificationUtils;
import org.had.hospitalinformationsystem.receptionist.Receptionist;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.utility.Utils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
@Slf4j
public class AuthServiceImpl extends AuthUtils implements AuthService {

    @Override
    public ResponseEntity<AuthResponse> createAdmin() {
        try {
            if (userRepository.findAdminByRole()) {
                return ResponseEntity.badRequest().body(new AuthResponse(null, "Admin already exists, cannot add another admin", null));
            }
            User user = createUserWithAdminDetails();
            userRepository.save(user);

            return ResponseEntity.ok(new AuthResponse("", "Registration Successful", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, "Error: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<Object> createUser(String jwt,  RegistrationDto registrationDto) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (!role.equals("admin")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, "Access denied", null));
            }

            Object result = getUser(registrationDto);
            if (result instanceof String) {
                return ResponseEntity.badRequest().body(new AuthResponse(null, (String) result, null));
            }

            User newUser = (User) result;
            newUser.setDisable(false);

            switch (registrationDto.getRole()) {
                case "doctor":
                    Object doctorResult = Utils.getDoctor(registrationDto, newUser);
                    if (doctorResult instanceof String) {
                        return ResponseEntity.badRequest().body(new AuthResponse(null, (String) doctorResult, null));
                    } else {
                        Doctor newDoctor = (Doctor) doctorResult;
                        saveUserAndDoctor(newUser, newDoctor);
                    }
                    break;
                case "receptionist":
                    Receptionist newReceptionist = new Receptionist();
                    newReceptionist.setUser(newUser);
                    saveUserAndReceptionist(newUser, newReceptionist);
                    break;
                case "nurse":
                    Nurse newNurse = new Nurse();
                    newNurse.setUser(newUser);
                    newNurse.setHeadNurse(registrationDto.isHeadNurse());
                    saveUserAndNurse(newUser, newNurse);
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, "Role doesn't exist", null));
            }
            newUser.setAuth(null);
            try {
                sendEmailWithAccountDetails(registrationDto.getEmail(), registrationDto.getUserName(), registrationDto.getPassword(), registrationDto.getFirstName());
                return ResponseEntity.ok(new AuthResponse("", "Registration Success", newUser));
            }
            catch(Exception e){
                return ResponseEntity.ok(new AuthResponse("", "User added Successfully, But failed to send mail", newUser));
            }

        }
        catch(DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponse(null,e.getMessage(),null));
        }
        catch(BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null,"Operation Failed due to Bad Credential",null));
        }
        catch (AuthenticationException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null,"Error adding User",null));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null,"Error: " + e.getMessage(),null));
        }
    }

    @Override
    public ResponseEntity< AuthResponse>signIn( LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticate(loginRequest.getUserName(), loginRequest.getPassword(), loginRequest.getRole());
            String token = JwtProvider.generateToken(authentication, loginRequest.getRole());
            String userName = JwtProvider.getUserNameFromJwtTokenUnfiltered(token);
            User user = userRepository.findByUserName(userName);
            if(user.isDisable()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null,"Access Denied: Kindly Contact to admin",null));
            }
            user.setAuth(null);
            return ResponseEntity.ok(new AuthResponse(token, "Login Success", user));
        }
        catch(AuthenticationException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, "Log In invalid, Log out and Try again", null));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, "Error: "+e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity< Map<String,String>> changePasswordByAdmin(String jwt,Long id) {
        Map<String,String> response = new HashMap<>();;
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("admin")) {
                Optional<User> optionalUser = userRepository.findById(id);
                User user;
                if(optionalUser.isPresent()) {
                    user = optionalUser.get();
                    return generateAndSentNewPasswordToUser(user);

                }
                else{
                    response.put("message","User doesnLdvf3Br1xD't exist");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            } else {
                response.put("message","Permission Denied");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }
        catch(Exception e){
            response.put("message","Error: "+e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @Override
    public ResponseEntity< String> changePasswordByUser(String jwt, ChangePasswordRequest changePasswordRequest) {
        try {
            String oldPassword = changePasswordRequest.getOldPassword();
            String newPassword = changePasswordRequest.getNewPassword();

            String userName = JwtProvider.getUserNameFromJwtToken(jwt);
            User currUser = userRepository.findByUserName(userName);

            if (Utils.verifyPassword(oldPassword, currUser.getAuth().getPassword(),currUser.getAuth().getSalt())) {
                String encodedNewPassword = Utils.hashPassword(newPassword,currUser.getAuth().getSalt());
                currUser.getAuth().setPassword(encodedNewPassword);
                userRepository.save(currUser);
                try {
                    sendEmailWithAcknowledgementOfPasswordChange(currUser.getEmail(), currUser.getUserName(), changePasswordRequest.getNewPassword(), currUser.getFirstName());
                    return ResponseEntity.ok("Password updated successfully");
                }
                catch(Exception e){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to send the mail to the user, Kindly do it manually ");
                }
            } else {
                return ResponseEntity.ok("Check your current password");
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error");
        }
    }

    @Override
    public ResponseEntity<?> sendOtpForForgetPasswordByUser(String emailId){
        Map<String,String> response = new HashMap<>();
        try{
            User currUser = userRepository.findUserByEmail(emailId);

            if(currUser != null){
                EmailOtpResponse emailOtpResponse =sendEmailForForgetPassword(currUser.getEmail(), currUser.getUserName(), currUser.getFirstName());
                response.put("message",emailOtpResponse.getMessage());
                if(emailOtpResponse.getStatus()==OtpStatus.DELIVERED){
                    return ResponseEntity.ok(response);
                }
                else{
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
            }
            else{
                response.put("message","Please Enter the Registered email");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        }catch (Exception e){
            response.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Override
    public ResponseEntity<Map<String,String>> validateOtpForForgetPasswordByUser(String emailId, String otp){
        Map<String, String> response = new HashMap<>();
        try{
            return validateOtp(emailId,otp);
        }
        catch(Exception e){
            response.put("message",e.getMessage());
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<?> toggleUserLogInStatus(String jwt,Long userId){
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if(role.equals("admin")){
            Optional<User> currUser = userRepository.findById(userId);
            if(currUser.isPresent()){
                User user = currUser.get();
                user.setDisable(!user.isDisable());
                userRepository.save(user);
                return ResponseEntity.ok(Map.of("message", "Status changed successfully", "status", user.isDisable()));
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No user present"));
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Access Denied"));
        }
    }
}
