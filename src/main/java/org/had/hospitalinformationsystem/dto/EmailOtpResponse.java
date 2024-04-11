package org.had.hospitalinformationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.had.hospitalinformationsystem.dto.OtpStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailOtpResponse {
    private OtpStatus status;
    private String message;

}
