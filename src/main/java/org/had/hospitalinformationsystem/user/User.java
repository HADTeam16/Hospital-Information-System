package org.had.hospitalinformationsystem.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String userName;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private Integer age;
    private String gender;
    private String dateOfBirth;
    private String country;
    private String state;
    private String city;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String pinCode;
    private String contact;
    @Column(unique = true)
    private String email;
    private String profilePicture;
    private String emergencyContactName;
    private String emergencyContactNumber;

    private String role;

    public User(String userName, String password, String firstName, String middleName, String lastName, Integer age,
            String gender, String dateOfBirth, String contact, String email, String role) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.contact = contact;
        this.email = email;
        this.role = role;
    }


}
