package org.had.hospitalinformationsystem.appointment;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
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
    @JoinColumn(name = "doctor_userid", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_userid", nullable = false)
    private Patient patient;

    @Lob
    @Column(columnDefinition="LONGTEXT", nullable = false)
    private String purpose;

    @NotNull // Validation: Slot cannot be null
    @Column(nullable = false)
    private LocalDateTime slot;

    @Min(value = 0, message = "Temperature cannot be negative")
    @Column(nullable = false)
    private float temperature;

    @NotNull // Validation: Blood pressure cannot be null
    @Column(nullable = false)
    private String bloodPressure;

    @Min(value = 0, message = "Weight cannot be negative")
    @Column(nullable = false)
    private float weight;

    @Min(value = 0, message = "HeartRate cannot be negative")
    @Column(nullable = false)
    private float heartRate;

    @NotNull
    @Column(nullable = false)
    private Boolean needWard;

    @NotNull
    @Column(nullable = false)
    private Integer completed;
}
