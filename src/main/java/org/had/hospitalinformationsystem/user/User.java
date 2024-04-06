package org.had.hospitalinformationsystem.user;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.had.hospitalinformationsystem.auth.Auth;

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

    @OneToOne
    @MapsId
    @JoinColumn(name="auth_authId")
    private Auth auth;

    @Column(nullable = false)
    private String firstName;

    private String middleName;

    private String lastName;

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

    @Column(nullable = false)
    private String emergencyContactName;

    @Column(nullable = false)
    private String emergencyContactNumber;

    @Column(nullable = false)
    private String role;

    private boolean isDisable;

}
