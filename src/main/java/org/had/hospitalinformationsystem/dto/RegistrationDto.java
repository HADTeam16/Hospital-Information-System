package org.had.hospitalinformationsystem.dto;


import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;

@Getter
@Setter
public class RegistrationDto {

        private String userName;
        private String password;
        private String firstName;
        private String middleName;
        private String lastName;
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
        private boolean isDisable;
        private String role;
        private String medicalLicenseNumber;
        private String specialization;
        private String boardCertification;
        private String medicalDegree;
        private String cv;
        private String drugScreeningResult;
        private LocalTime workStart;
        private LocalTime workEnd;
        private float temperature;
        private String bloodPressure;
        private float weight;
        private float heartRate;
        private boolean consent;
        private String bloodGroup;
        private String height;
        private boolean headNurse;






}
