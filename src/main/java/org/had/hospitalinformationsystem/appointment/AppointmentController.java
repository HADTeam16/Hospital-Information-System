package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.doctor.DoctorService;
import org.had.hospitalinformationsystem.dto.AppointmentDataDto;
import org.had.hospitalinformationsystem.dto.AppointmentDto;
import org.had.hospitalinformationsystem.dto.AppointmentResponseDto;
import org.had.hospitalinformationsystem.dto.PrescriptionsAndRecords;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.prescription.PrescriptionRepository;
import org.had.hospitalinformationsystem.records.RecordsRepository;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    AppointmentService appointmentService;

    @Autowired
    DoctorService doctorService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/appointmentBooked")
    @SendTo("/topic/appointments")
    public void handleAppointmentBooking(Appointment appointment) {
        Doctor doctor = appointment.getDoctor();
        if (doctor != null) {
            String doctorTopic = "/topic/doctor/" + doctor.getDoctorId() + "/appointments";
            messagingTemplate.convertAndSend(doctorTopic, appointment);
        }
    }

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
}
