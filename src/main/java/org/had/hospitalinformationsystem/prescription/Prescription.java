package org.had.hospitalinformationsystem.prescription;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.appointment.Appointment;

@Entity
@Getter
@Setter
@Table(name = "prescriptions")
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prescriptionId;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    @Lob
    @Column(columnDefinition="LONGTEXT")
    private String prescription;

}