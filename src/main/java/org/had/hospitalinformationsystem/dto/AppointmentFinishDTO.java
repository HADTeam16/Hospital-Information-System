package org.had.hospitalinformationsystem.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AppointmentFinishDTO {
    private Long appointmentId;
    private String prescription;
    private List<String> records;
    private Boolean needWard;
}
