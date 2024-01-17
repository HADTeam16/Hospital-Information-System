package org.had.hospitalinformationsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String id;
    private String firstName;
    private String lastName;
    @Id
    private String userName;
    private String password;
    private Integer age;
    private String role;
    private String contact;

}
