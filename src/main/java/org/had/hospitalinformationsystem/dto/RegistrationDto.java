package org.had.hospitalinformationsystem.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDto {

        // Common User fields
        private String userName;
        private String password;
        private String firstName;
        private String lastName;
        private Integer age;
        private String gender;
        private String dateOfBirth;
        private String address;
        private String contact;
        private String email;
        private String profilePicture;
        private String role;

        // Doctor-specific field
        private String specialization;

        //Patient-specific field
        private  String temperature;



}
