package org.had.hospitalinformationsystem.needWard;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.appointment.Appointment;


@Entity
@Getter
@Setter
@Table(name = "needWard")
public class NeedWard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long needWardId;

    @OneToOne
    @JoinColumn(name = "appointment_appointmentId")
    private Appointment appointment;
}
