package org.had.hospitalinformationsystem.model;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Users {

    private String id;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private Integer age;
    private String role;
    private String contact;
    private String userid;
}
