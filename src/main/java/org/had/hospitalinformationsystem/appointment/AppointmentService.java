package org.had.hospitalinformationsystem.appointment;

import java.time.LocalDateTime;

public interface AppointmentService {
    Appointment createAppointment(AppointmentDto appointmentDto);

}
