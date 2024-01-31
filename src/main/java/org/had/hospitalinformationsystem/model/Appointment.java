package org.had.hospitalinformationsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    @ManyToOne
    @JoinColumn(name = "doctor_userid")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_userid")
    private Patient patient;

    private LocalDateTime slot;

}
