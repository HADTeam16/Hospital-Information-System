package org.had.hospitalinformationsystem.otpVerification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsOtpRequest {
    private String username;
    private String phoneNumber;

}
