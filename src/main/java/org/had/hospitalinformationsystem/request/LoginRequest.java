package org.had.hospitalinformationsystem.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String userName;
    private String password;
    private String role;
}
