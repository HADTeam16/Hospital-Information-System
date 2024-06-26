package org.had.hospitalinformationsystem.patient;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.user.User;

import java.time.LocalDateTime;

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
    private LocalDateTime registrationDateAndTime;
    private float temperature;
    private String bloodPressure;
    private float weight;
    private float heartRate;
    private String bloodGroup;
    private String height;
    private boolean consent;
}
