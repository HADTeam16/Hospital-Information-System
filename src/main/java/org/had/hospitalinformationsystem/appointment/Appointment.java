package org.had.hospitalinformationsystem.appointment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.patient.Patient;

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
    @Lob
    @Column(columnDefinition="LONGTEXT")
    private String purpose;
    private LocalDateTime slot;
    private float temperature;
    private String bloodPressure;
    private float weight;
    private float height;
    private Boolean needWard;
    private Integer completed;
}
