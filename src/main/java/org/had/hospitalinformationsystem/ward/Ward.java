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
@Table(name="ward")
public class Ward {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long wardId;
    private int floor;
    private String wardNumber;
    private boolean availableStatus;
    @Enumerated(EnumType.STRING)
    private VipDeluxeStatus vipDeluxeStatus;
    @OneToOne
    @JoinColumn(name = "appointment_id", referencedColumnName = "appointmentId")
    private Appointment appointment;
    public enum VipDeluxeStatus {
        STANDARD,
        VIP,
        DELUXE
    }
    @ManyToOne
    @JoinColumn(name = "nurse_id")
    private Nurse managingNurse;
    @OneToOne
    private Patient patient;

}
