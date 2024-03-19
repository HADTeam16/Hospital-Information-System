package org.had.hospitalinformationsystem.ward;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.nurse.Nurse;
import org.had.hospitalinformationsystem.patient.Patient;

@Entity
@Getter
@Setter
@Table(name="needWard")
public class WardDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long wardDetailsId;
    private int floor;
    private String wardNumber;
    private boolean availableStatus;

}
