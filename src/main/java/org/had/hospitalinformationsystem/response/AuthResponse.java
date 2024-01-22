package org.had.hospitalinformationsystem.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private String message;

    public AuthResponse(String token, String message) {
        super();
        this.token = token;
        this.message = message;
    }
}
