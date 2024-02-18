package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.dto.AppointmentDto;

public interface AppointmentService {
    Appointment createAppointment(AppointmentDto appointmentDto);

}
