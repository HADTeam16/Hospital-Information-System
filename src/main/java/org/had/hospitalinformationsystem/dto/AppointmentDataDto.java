package org.had.hospitalinformationsystem.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentDataDto {
    Long appointmentId;
    LocalDateTime dateTime;

}
