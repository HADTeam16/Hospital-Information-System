package org.had.hospitalinformationsystem.auth;

import lombok.extern.slf4j.Slf4j;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.dto.AuthResponse;
import org.had.hospitalinformationsystem.dto.ChangePasswordRequest;
import org.had.hospitalinformationsystem.dto.LoginRequest;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.nurse.Nurse;
import org.had.hospitalinformationsystem.otpVerification.*;
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

            Object result = utils.getUser(registrationDto);
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
    public ResponseEntity< String> changePasswordByAdmin(String jwt, ChangePasswordRequest changePasswordRequest) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("admin")) {
                User user = userRepository.findByUserName(changePasswordRequest.getUserName());
                String encodedNewPassword = Utils.hashPassword(changePasswordRequest.getNewPassword(),user.getAuth().getSalt());
                user.getAuth().setPassword(encodedNewPassword);
                userRepository.save(user);
                try {
                    sendEmailWithNewPasswordDetails(user.getEmail(), user.getUserName(), changePasswordRequest.getNewPassword(), user.getFirstName());
                    return ResponseEntity.ok("Password updated successfully");
                }
                catch(Exception e){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to send the mail to the user, Kindly do it manually ");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Permission Denied");
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: "+e.getMessage());
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
        try{
            User currUser = userRepository.findUserByEmail(emailId);
            if(currUser != null){
                return ResponseEntity.ok(sendEmailForForgetPassword(currUser.getEmail(), currUser.getUserName(), currUser.getFirstName()));
            }
            else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Please Enter the Registered email");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> validateOtpForForgetPasswordByUser(String emailId, String otp){
        try{
            User user = userRepository.findUserByEmail(emailId);
            EmailOtpValidationRequest emailOtpValidationRequest = new EmailOtpValidationRequest();
            emailOtpValidationRequest.setUsername(user.getUserName());
            emailOtpValidationRequest.setEmailOtpNumber(otp);
            ForgetPasswordEmailResponse response = validateOtp(emailOtpValidationRequest,emailId);
            if(response.getIsSent()==1){
                return ResponseEntity.ok(response.getStatus());
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.getStatus());
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
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
