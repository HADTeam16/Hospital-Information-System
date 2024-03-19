package org.had.hospitalinformationsystem.patient;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.user.User;

@Getter
@Setter
@Entity
@Table(name="patient")
public class Patient {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name="user_id")
    private User user;
    private String purpose;
    private String temperature;
    private String bloodPressure;
    private String admissionDate;
    private String dischargeDate;
    private Boolean needWard;



}
