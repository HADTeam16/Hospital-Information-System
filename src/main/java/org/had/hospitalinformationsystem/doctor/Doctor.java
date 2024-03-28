package org.had.hospitalinformationsystem.doctor;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.OT.OT;
import org.had.hospitalinformationsystem.user.User;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter

@Table(name="doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long doctorId;

    @OneToOne
    @MapsId
    @JoinColumn(name="user_id")
    private User user;

    private String medicalLicenseNumber;
    private String specialization;
    private String boardCertification; // Doc
    private String experience;
    private String medicalDegree; //Doc
    private String cv; //Doc
    private String drugScreeningResult; // Doc
    private LocalTime workStart;
    private LocalTime workEnd;


}
