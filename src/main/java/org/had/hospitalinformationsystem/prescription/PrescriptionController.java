package org.had.hospitalinformationsystem.prescription;

import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PrescriptionRepository prescriptionRepository;

    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    PatientRepository patientRepository;


    @PostMapping("/add/prescription/{appointmentId}")
    public ResponseEntity<Prescription> addPrescription(@RequestHeader("Authorization") String jwt, @RequestBody String prescription, @PathVariable Long appointmentId){
        try{
            String role=JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("doctor")){
                Prescription prescription1=new Prescription();
                prescription1.setPrescription(prescription);
                Appointment appointment=appointmentRepository.findByAppointmentId(appointmentId);
                prescription1.setAppointment(appointment);
                prescriptionRepository.save(prescription1);
                return ResponseEntity.ok(prescription1);
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

    }
    @GetMapping("/get/prescription/from/appointment")
    public ResponseEntity<Prescription> getPrescriptionFromAppointment(@RequestHeader("Authorization") String jwt,@RequestParam Long appointmentId){
        try{
            String role=JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("doctor")){
                Prescription prescription=prescriptionRepository.findPrescriptionByAppointmentID(appointmentId);
                return ResponseEntity.ok(prescription);
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
