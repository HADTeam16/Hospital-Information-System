package org.had.hospitalinformationsystem.otpVerification;

import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpInfo {
    public String otp;
    public Instant expirationTime;

}
