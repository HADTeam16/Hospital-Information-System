package org.had.hospitalinformationsystem.appointment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentDto {
    private Long doctorId;
    private Long patientId;
    private String purpose;
    private LocalDateTime slot;
}
