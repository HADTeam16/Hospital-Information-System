package org.had.hospitalinformationsystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentDto {
    private Long doctorId;
    private Long patientId;
    private String purpose;
    private float temperature;
    private String bloodPressure;
    private float weight;
    private float heartRate;
}
