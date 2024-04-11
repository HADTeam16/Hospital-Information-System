package org.had.hospitalinformationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpValidationRequest {
    private String username;
    private String otpNumber;
}
