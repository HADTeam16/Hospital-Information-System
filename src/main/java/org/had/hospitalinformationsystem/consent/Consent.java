package org.had.hospitalinformationsystem.consent;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.patient.Patient;

@Getter
@Setter
@Entity
@Table(name = "consent")
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long concentId;

    @OneToOne
    @JoinColumn(name="patient_userid")
    private Patient patient;

    private boolean isConcent;

}
