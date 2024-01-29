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
    private String dataOfBirth;
    private String address;
    private String contact;
    @Column(unique = true)
    private String email;
    private String profilePicture;

    private String role;
    private String specialization;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", dataOfBirth='" + dataOfBirth + '\'' +
                ", address='" + address + '\'' +
                ", contact='" + contact + '\'' +
                ", email='" + email + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", role='" + role + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}
