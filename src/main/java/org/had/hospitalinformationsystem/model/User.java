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
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String userName;
    private String password;
    private Integer age;
    private String role;
    private String contact;
    private String avatarUrl; // URL or path to the image file
    private String specialization;

}
