package org.had.hospitalinformationsystem.assignWard;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.nurse.Nurse;
import org.had.hospitalinformationsystem.ward.WardDetails;

@Entity
@Getter
@Setter
@Table(name = "assignWard")
public class AssignWard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long assignWardId;
    @OneToOne
    @JoinColumn(name = "appointment_appointmentId")
    private Appointment appointment;

    @OneToOne
    @JoinColumn(name = "wardDetails_wardDetailsId")
    private WardDetails wardDetails;


    @ManyToOne
    @JoinColumn(name = "nurse_nurseId")
    private Nurse nurse;

}
