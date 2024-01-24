package org.had.hospitalinformationsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long appointmentId;
    @ManyToOne
    @JoinColumn(name="doctor_id", referencedColumnName = "doctorid")
    private User doctor;

    @ManyToOne
    @JoinColumn(name="patient_id",referencedColumnName = "patientid")
    private User patient;

    private LocalDateTime slot;




}
