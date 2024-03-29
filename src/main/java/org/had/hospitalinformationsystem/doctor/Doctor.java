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
    @Column(nullable = false)
    private String medicalLicenseNumber;
    @Column(nullable = false)
    private String specialization;
    @Lob
    @Column(columnDefinition="LONGTEXT", nullable = false)
    private String boardCertification; // Doc
    @Column(nullable = false)
    private String experience;
    @Lob
    @Column(columnDefinition="LONGTEXT", nullable = false)
    private String medicalDegree; //Doc
    @Lob
    @Column(columnDefinition="LONGTEXT", nullable = false)
    private String cv; //Doc
    @Lob
    @Column(columnDefinition="LONGTEXT",nullable = false)
    private String drugScreeningResult; // Doc
    @Column(nullable = false)
    private LocalTime workStart;
    @Column(nullable = false)
    private LocalTime workEnd;
}
