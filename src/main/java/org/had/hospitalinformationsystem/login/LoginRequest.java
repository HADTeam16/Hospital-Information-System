package org.had.hospitalinformationsystem.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String userName;
    private String password;
    private String role;
}
