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
    @Lob
    @Column(columnDefinition="LONGTEXT")
    private String boardCertification; // Doc
    private String experience;
    @Lob
    @Column(columnDefinition="LONGTEXT")
    private String medicalDegree; //Doc
    @Lob
    @Column(columnDefinition="LONGTEXT")
    private String cv; //Doc
    @Lob
    @Column(columnDefinition="LONGTEXT")
    private String drugScreeningResult; // Doc
    private LocalTime workStart;
    private LocalTime workEnd;


}
