package org.had.hospitalinformationsystem.dto;

import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpInfo {
    public String otp;
    public Instant expirationTime;

}
