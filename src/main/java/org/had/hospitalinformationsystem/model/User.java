package org.had.hospitalinformationsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private Integer age;
    private String gender;
    private String dateOfBirth;
    private String address;
    private String contact;
    @Column(unique = true)
    private String email;
    private String profilePicture;

    private String role;


}
