package org.had.hospitalinformationsystem.dto;

import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.user.User;

@Getter
@Setter
public class AuthResponse {

    private String token;
    private String message;
    private User user;


    public AuthResponse(String token, String message, User user) {
        super();
        this.token = token;
        this.message = message;
        this.user=user;

    }
}