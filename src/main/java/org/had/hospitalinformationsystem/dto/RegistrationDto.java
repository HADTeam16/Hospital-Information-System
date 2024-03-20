package org.had.hospitalinformationsystem.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class RegistrationDto {

        // Common User fields
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
        private String email;
        private String profilePicture;
        private String emergencyContactName;
        private String emergencyContactNumber;

        private String role;

        // Doctor-specific field
        private String medicalLicenseNumber;
        private String specialization;
        private String boardCertification; // Doc
        private String experience;
        private String medicalDegree; //Doc
        private String cv; //Doc
        private String drugScreeningResult; // Doc
        private LocalTime workStart;
        private LocalTime workEnd;

        //Patient-specific field
        private LocalDateTime registrationDateAndTime;

        //Appointment field
        private String temperature;
        private String bloodPressure;
        private String admissionDate;
        private String dischargeDate;
        private String purpose;
        
        //Nurse-specific field
        private boolean headNurse;






}
