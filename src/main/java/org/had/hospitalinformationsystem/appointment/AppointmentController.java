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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
    PatientRepository patientRepository;

    @Autowired
    RecordsRepository recordsRepository;

    @Autowired
    PrescriptionRepository prescriptionRepository;

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

    @GetMapping("/get/all/appointments/by/date")
    public ResponseEntity<List<Appointment>>getAllAppointmentByDate(@RequestHeader("Authorization") String jwt,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        try{
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            String userName = JwtProvider.getUserNameFromJwtToken(jwt);
            User user = userRepository.findByUserName(userName);

            if(role.equals("doctor")){
                LocalDateTime startDate = date.atStartOfDay();
                LocalDateTime endDate = startDate.plusDays(1);
                return ResponseEntity.ok(appointmentRepository.findByDoctorIdAndAppointmentDate(user.getId(),startDate,endDate));
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch(Exception e){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    @GetMapping("/get/patient/details")
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
            return ResponseEntity.internalServerError().body("An error occurred while fetching appointments: " + e.getMessage());
        }
    }

    @PostMapping("/book/appointment")
    public ResponseEntity<?> bookAppointment(@RequestHeader("Authorization") String jwt, @RequestBody AppointmentDto appointmentDto) {
        String userName = JwtProvider.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUserName(userName);
        AppointmentResponseDto appointmentResponseDto = new AppointmentResponseDto();
        if (!user.getRole().equals("receptionist")) {
            appointmentResponseDto.setResponse("Only receptionist can book an appointment");
            return ResponseEntity.badRequest().body(appointmentResponseDto);
        }
        Appointment appointment;
        try {
            appointment = appointmentService.createAppointment(appointmentDto);
        } catch (Exception e) {
            appointmentResponseDto.setResponse("Failed to create appointment: " + e.getMessage());
            return ResponseEntity.badRequest().body(appointmentResponseDto);
        }
        try {
            Doctor doctor = appointment.getDoctor();
            messagingTemplate.convertAndSendToUser(doctor.getUser().getUserName(),"/topic/appointments", appointment);
        } catch (Exception e) {
            appointmentResponseDto.setResponse("Failed to send WebSocket update for appointment: " + e.getMessage());
            return ResponseEntity.ok().body(appointmentResponseDto);
        }

        appointmentResponseDto.setResponse("Appointment created successfully for: " + appointment.getSlot().toString());
        return ResponseEntity.ok().body(appointmentResponseDto);
    }
    @GetMapping("/get/all/previous/appointment/for/patient")
    public ResponseEntity<List<AppointmentDataDto>>getAllPreviousAppointmentForPatient(@RequestHeader("Authorization") String jwt, @RequestParam("patientId") Long patientId, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date){
        try{
            String role=JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("doctor")){
                List<Object[]> appointments=appointmentRepository.findAllPreviousAppointmentForPatient(patientId,date);

                List<AppointmentDataDto> appointmentDataDtos = new ArrayList<>();
                for (Object[] appointment : appointments) {
                    AppointmentDataDto dto = new AppointmentDataDto();
                    dto.setAppointmentId((Long) appointment[0]); // Assuming appointmentId is at index 0
                    dto.setDateTime((LocalDateTime) appointment[1]); // Assuming dateTime is at index 1
                    appointmentDataDtos.add(dto);
                }
                return ResponseEntity.ok(appointmentDataDtos);
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/get/appointment/prescription/records/{appointmentId}")
    public ResponseEntity<PrescriptionsAndRecords> getAppointmentPrescriptionAndRecords(
            @RequestHeader("Authorization") String jwt, @PathVariable Long appointmentId) {

        String role = JwtProvider.getRoleFromJwtToken(jwt);

        if (role.equals("doctor")) {
            List<String> records = recordsRepository.findRecordsImageByAppointmentId(appointmentId);
            String prescription = prescriptionRepository.findPrescriptionImageByAppointmentID(appointmentId);
            Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId);
            PrescriptionsAndRecords appointmentDetails = new PrescriptionsAndRecords(records, prescription,appointment);
            return ResponseEntity.ok(appointmentDetails);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
