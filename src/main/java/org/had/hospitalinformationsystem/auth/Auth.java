package org.had.hospitalinformationsystem.auth;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="auth")
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long authId;
    private String password;
}
