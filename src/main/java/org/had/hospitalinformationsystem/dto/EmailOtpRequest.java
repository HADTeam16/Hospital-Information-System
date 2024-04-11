package org.had.hospitalinformationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailOtpRequest {
    private String username;
    private String email;
    private String name;
}
