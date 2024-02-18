package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.doctor.DoctorService;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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



    @GetMapping("/getallappointment")
    public List<Appointment>getAllAppointment(@RequestHeader("Authorization") String jwt){
        return appointmentRepository.findAll();
    }


    @GetMapping("/patientDetails")
    public  List<Patient>getDoctorsAppointment(@RequestHeader("Authorization") String jwt){
        String userName= JwtProvider.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUserName(userName);
        Doctor doctor = doctorRepository.findByUser(user);
        return appointmentRepository.getDoctorsAppointment(doctor.getDoctorId());
    }

    //API to schedule an appointment
    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentDto appointmentDto){
//        Optional<Doctor> doctorOpt=doctorRepository.findById(appointmentDto.getDoctorId());
//        if(appointmentDto.getSlot().toLocalTime().isBefore(doctorOpt.get().getWorkStart())||
//        appointmentDto.getSlot().plusHours(1).toLocalTime().isAfter(doctorOpt.get().getWorkEnd())){
//            return ResponseEntity.badRequest().body("Appointment time is outside of Doctor's working hours");
//        }
//        if(!doctorService.isDoctorAvailable(appointmentDto.getDoctorId(),appointmentDto.getSlot())){
//            return ResponseEntity.badRequest().body("Doctor is not available at the  desired time.");
//        }

        try{
            Appointment appointment=appointmentService.createAppointment(appointmentDto);
            return ResponseEntity.ok().body("Appointment created successfully for: "+appointment.getSlot().toString());
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body("Failed to create appointment"+e.getMessage());
        }
    }

    //API to give list of available doctor at current time slot or approximate time slot. Instead of time we can go by indexing.
//    @GetMapping("/available-doctors")
//    public ResponseEntity<List<Doctor>>getAvailableDoctor(@RequestParam String specialization,
//    @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime slot){
//        List<Doctor> availableDoctors=doctorService.getAvailableDoctorsBySpecializationAndSlot(specialization,slot);
//        return ResponseEntity.ok(availableDoctors);
//    }

    //API to give list of available doctor at some given time slot.

}
