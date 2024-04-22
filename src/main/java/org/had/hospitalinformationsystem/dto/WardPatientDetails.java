package org.had.hospitalinformationsystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WardPatientDetails {
    private float temperature;
    private String bloodPressure;
    private float weight;
    private float heartRate;
}
