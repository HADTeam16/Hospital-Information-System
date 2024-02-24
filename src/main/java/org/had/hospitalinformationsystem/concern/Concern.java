package org.had.hospitalinformationsystem.concern;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.patient.Patient;

@Getter
@Setter
@Entity
@Table(name = "concern")
public class Concern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long concernId;

    @OneToOne
    @JoinColumn(name="patient_userid")
    private Patient patient;

    private boolean isConcern;

}
