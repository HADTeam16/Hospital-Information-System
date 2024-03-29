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
    @Column(nullable = false)
    private String firstName;
    private String middleName;
    private String lastName;
    @Column(nullable = false)
    private Integer age;
    @Column(nullable = false)
    private String gender;
    @Column(nullable = false)
    private String dateOfBirth;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    @Column(nullable = false)
    private String pinCode;
    @Column(nullable = false)
    private String contact;
    @Column(unique = true)
    private String email;
    @Lob
    @Column(columnDefinition="LONGTEXT")
    private String profilePicture;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String salt;
    @Column(nullable = false)
    private String role;
    private boolean isDisable;


    public boolean isValid() {
        // Check if all required fields are present
        if (userName == null || password == null || firstName == null || age == null || gender == null ||
                dateOfBirth == null || country == null || state == null || city == null || addressLine1 == null ||
                pinCode == null || contact == null || email == null ||  role == null) {
            return false;
        }
        if (!gender.equalsIgnoreCase("male") && !gender.equalsIgnoreCase("female") &&
                !gender.equalsIgnoreCase("dontSpecify")) {
            return false;
        }
        if (!contact.matches("\\d{10}")) {
            return false;
        }

        if (emergencyContactName != null && !emergencyContactNumber.matches("\\d{10}")) {
            return false;
        }
        return true;
    }


}
