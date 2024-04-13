package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.dto.AppointmentDto;
import org.had.hospitalinformationsystem.dto.PrescriptionsAndRecords;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {

    ResponseEntity<List<Appointment>> getAllAppointments(String jwt);

    ResponseEntity<List<Appointment>> getAllAppointmentsByDate(String jwt, LocalDate date);

    ResponseEntity<?> getDoctorsAppointments(String jwt);

    ResponseEntity<?> bookAppointment(String jwt, AppointmentDto appointmentDto);

    ResponseEntity<?> getAllPreviousAppointmentsForPatient(String jwt, Long patientId, LocalDateTime date);

    ResponseEntity<PrescriptionsAndRecords> getAppointmentDetails(String jwt,Long appointmentId);

    ResponseEntity<String> cancelAppointment(String jwt,Long appointmentId);
}
