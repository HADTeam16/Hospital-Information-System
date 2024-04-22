package org.had.hospitalinformationsystem.wardHistory;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.ward.Ward;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class WardHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long wardHistoryId;
    private LocalDateTime log;
    @ManyToOne
    @JoinColumn(name = "appointment_id", referencedColumnName = "appointmentId")
    private Appointment appointment;
    private float temperature;
    private String bloodPressure;
    private float weight;
    private float heartRate;

    public void setAppointment(Appointment appointment) {
        this.appointment=appointment;
    }
}
