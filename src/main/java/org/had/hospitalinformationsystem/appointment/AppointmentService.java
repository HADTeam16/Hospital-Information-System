package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.dto.AppointmentDto;
import org.had.hospitalinformationsystem.dto.PrescriptionsAndRecords;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    Appointment createAppointment(AppointmentDto appointmentDto);

    public ResponseEntity<List<Appointment>> getAllAppointments(String jwt);
    public ResponseEntity<List<Appointment>> getAllAppointmentsByDate(String jwt, LocalDate date);

    public ResponseEntity<?> getDoctorsAppointments(String jwt);
    public ResponseEntity<?> bookAppointment(String jwt, AppointmentDto appointmentDto);


    void notifyDoctor(Appointment appointment);
    public ResponseEntity<?> getAllPreviousAppointmentsForPatient(String jwt, Long patientId, LocalDateTime date);
    public ResponseEntity<PrescriptionsAndRecords> getAppointmentDetails(String jwt,Long appointmentId);
}
