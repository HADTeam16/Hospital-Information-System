package org.had.hospitalinformationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailOtpResponse {
    private OtpStatus status;
    private String message;

}
