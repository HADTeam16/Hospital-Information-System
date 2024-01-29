package org.had.hospitalinformationsystem.response;

import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.config.JwtProvider;
import org.had.hospitalinformationsystem.model.User;
import org.had.hospitalinformationsystem.repository.UserRepository;
import org.had.hospitalinformationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
public class AuthResponse {

    private String token;
    private String message;
    private User user;


    public AuthResponse(String token, String message,User user) {
        super();
        this.token = token;
        this.message = message;

        this.user=user;

    }
}