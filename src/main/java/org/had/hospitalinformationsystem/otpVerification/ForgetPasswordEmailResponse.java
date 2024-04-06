package org.had.hospitalinformationsystem.otpVerification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordEmailResponse {
    String status;
    int isSent;
    String password;
}
