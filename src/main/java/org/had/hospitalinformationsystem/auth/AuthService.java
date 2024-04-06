package org.had.hospitalinformationsystem.auth;

import org.had.hospitalinformationsystem.otpVerification.EmailOtpResponse;
import org.had.hospitalinformationsystem.otpVerification.EmailOtpValidationRequest;
import org.had.hospitalinformationsystem.otpVerification.ForgetPasswordEmailResponse;
import org.had.hospitalinformationsystem.user.User;

public interface AuthService {

    void sendEmailWithAccountDetails(String email, String username, String password, String name);

    void sendEmailWithNewPasswordDetails(String email, String username, String password, String name);

    void sendEmailWithAcknowledgementOfPasswordChange(String email, String username, String password, String name);

    EmailOtpResponse sendEmailForForgetPassword(User user);

    ForgetPasswordEmailResponse validateOtp(EmailOtpValidationRequest emailOtpValidationRequest, String email);
}
