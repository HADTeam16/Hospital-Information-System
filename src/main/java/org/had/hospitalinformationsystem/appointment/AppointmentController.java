package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.doctor.DoctorService;
import org.had.hospitalinformationsystem.dto.AppointmentDto;
import org.had.hospitalinformationsystem.dto.PrescriptionsAndRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    AppointmentService appointmentService;
    @Autowired
    DoctorService doctorService;

    @GetMapping("/get/all/appointments")
    public ResponseEntity< List<Appointment>>getAllAppointment(@RequestHeader("Authorization") String jwt){
        return appointmentService.getAllAppointments(jwt);
    }

    @GetMapping("/get/all/appointments/by/date")
    public ResponseEntity<List<Appointment>>getAllAppointmentByDate(@RequestHeader("Authorization") String jwt,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return appointmentService.getAllAppointmentsByDate(jwt,date);
    }

    @GetMapping("/get/patient/details")
    public ResponseEntity<?> getDoctorsAppointment(@RequestHeader("Authorization") String jwt) {
        return appointmentService.getDoctorsAppointments(jwt);
    }

    @PostMapping("/book/appointment")
    public ResponseEntity<?> bookAppointment(@RequestHeader("Authorization") String jwt, @RequestBody AppointmentDto appointmentDto) {
        return appointmentService.bookAppointment(jwt,appointmentDto);
    }

    @GetMapping("/get/all/previous/appointment/for/patient")
    public ResponseEntity<?>getAllPreviousAppointmentForPatient(@RequestHeader("Authorization") String jwt, @RequestParam("patientId") Long patientId, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date){
        return appointmentService.getAllPreviousAppointmentsForPatient(jwt, patientId, date);
    }

    @GetMapping("/get/appointment/prescription/records/{appointmentId}")
    public ResponseEntity<PrescriptionsAndRecords> getAppointmentPrescriptionAndRecords(@RequestHeader("Authorization") String jwt, @PathVariable Long appointmentId) {
        return appointmentService.getAppointmentDetails(jwt,appointmentId);
    }
    @GetMapping("/cancel/appointment/{appointmentId}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@RequestHeader("Authorization") String jwt, @PathVariable Long appointmentId){
        return appointmentService.cancelAppointment(jwt,appointmentId);
    }
}
