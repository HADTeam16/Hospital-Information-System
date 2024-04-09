package org.had.hospitalinformationsystem.auth;

import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.nurse.Nurse;
import org.had.hospitalinformationsystem.otpVerification.EmailOtpResponse;
import org.had.hospitalinformationsystem.otpVerification.EmailOtpValidationRequest;
import org.had.hospitalinformationsystem.otpVerification.ForgetPasswordEmailResponse;
import org.had.hospitalinformationsystem.receptionist.Receptionist;
import org.had.hospitalinformationsystem.user.User;
import org.springframework.security.core.Authentication;

public interface AuthService {

    User createUserWithAdminDetails();
    void saveUserAndDoctor(User newUser, Doctor newDoctor);

    void saveUserAndReceptionist(User newUser, Receptionist newReceptionist);
    void saveUserAndNurse(User newUser, Nurse newNurse);

    void sendEmailWithAccountDetails(String email, String username, String password, String name);

    void sendEmailWithNewPasswordDetails(String email, String username, String password, String name);

    void sendEmailWithAcknowledgementOfPasswordChange(String email, String username, String password, String name);

    EmailOtpResponse sendEmailForForgetPassword(String email, String username, String password, String name);

    ForgetPasswordEmailResponse validateOtp(EmailOtpValidationRequest emailOtpValidationRequest, String email);

    Authentication authenticate(String userName, String password, String role);
}
