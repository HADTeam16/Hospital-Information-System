package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.doctor.DoctorService;
import org.had.hospitalinformationsystem.dto.AppointmentDto;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AppointmentRepository  appointmentRepository;

    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    AppointmentService appointmentService;
    @Autowired
    DoctorService doctorService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/appointmentBooked")
    @SendTo("/topic/appointments")
    public Appointment handleAppointmentBooking(Appointment appointment) {
        return appointment;
    }


    @GetMapping("/getallappointment")
    public ResponseEntity< List<Appointment>>getAllAppointment(@RequestHeader("Authorization") String jwt){
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("receptionist")) {
                return ResponseEntity.ok(appointmentRepository.findAll());
            }
            else{
                return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch(Exception e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/patientDetails")
    public ResponseEntity<?> getDoctorsAppointment(@RequestHeader("Authorization") String jwt) {
        try {
            String userName = JwtProvider.getUserNameFromJwtToken(jwt);
            User user = userRepository.findByUserName(userName);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            Doctor doctor = doctorRepository.findByUser(user);
            if (doctor == null) {
                return ResponseEntity.badRequest().body("Doctor not found for user: " + userName);
            }
            List<Patient> appointments = appointmentRepository.getDoctorsAppointment(doctor.getDoctorId());
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            // Log the exception or handle it as per your application's error handling policy
            return ResponseEntity.internalServerError().body("An error occurred while fetching appointments: " + e.getMessage());
        }
    }

    //API to schedule an appointment
    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestHeader("Authorization") String jwt, @RequestBody AppointmentDto appointmentDto) {
        String userName = JwtProvider.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUserName(userName);
        if (!user.getRole().equals("receptionist")) {
            return ResponseEntity.badRequest().body("Only receptionist can book an appointment");
        }
        Appointment appointment = null;
        try {
            appointment = appointmentService.createAppointment(appointmentDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create appointment: " + e.getMessage());
        }

        // Attempt to send WebSocket message in a separate try-catch to handle messaging errors specifically
        try {
            messagingTemplate.convertAndSend("/topic/appointments", appointment);
        } catch (Exception e) {
            // Log the error and continue. The appointment is booked, but the real-time notification failed.
            // Consider logging this exception to a log file or monitoring service
            System.err.println("Failed to send WebSocket update for appointment: " + e.getMessage());
            // Optionally, inform the caller that the booking was successful but updates may be delayed.
            // This is a design choice depending on how critical real-time updates are for your application.
        }

        // If we reach here, the appointment was successfully created, regardless of WebSocket success.
        return ResponseEntity.ok().body("Appointment created successfully for: " + appointment.getSlot().toString());
    }

    //API to give list of available doctor at current time slot or approximate time slot. Instead of time we can go by indexing.

    //API to give list of available doctor at some given time slot.


}
