package org.had.hospitalinformationsystem.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentFinishDTO {
    private final Long appointmentId;
    private final String prescription;
    private final List<String> records;
    private final Boolean needWard;

    @JsonCreator  // For clarity, mark the constructor Jackson should use
    public AppointmentFinishDTO(
            @JsonProperty("appointmentId") Long appointmentId,
            @JsonProperty("prescription") String prescription,
            @JsonProperty("records") List<String> records,
            @JsonProperty("needWard") Boolean needWard
    ) {
        this.appointmentId = appointmentId;
        this.prescription = prescription;
        this.records = records;
        this.needWard = needWard;
    }
}