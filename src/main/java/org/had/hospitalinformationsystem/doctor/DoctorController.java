package org.had.hospitalinformationsystem.doctor;

import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.needWard.NeedWard;
import org.had.hospitalinformationsystem.needWard.NeedWardRepository;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    NeedWardRepository needWardRepository;

    @GetMapping("/get/all/doctors")
    public ResponseEntity<?> getAllDoctor(@RequestHeader("Authorization") String jwt) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (!role.equals("admin") && !role.equals("receptionist")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Unauthorized role");
            }
            List<Doctor> allDoctor = doctorRepository.findAll();
            if (allDoctor.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(allDoctor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve doctors: " + e.getMessage());
        }
    }

    @GetMapping("/recommend/ward/{appointmentId}")
    public ResponseEntity<?> assignWard(@RequestHeader("Authorization") String jwt, @PathVariable long appointmentId){
        String role=JwtProvider.getRoleFromJwtToken(jwt);
        if(role.equals("doctor")){
            Appointment appointment=appointmentRepository.findByAppointmentId(appointmentId);
            if(appointment==null){
                return ResponseEntity.badRequest().body("Appointment id not found");
            }
            //Patient patient=appointment.getPatient();
            NeedWard needWard=new NeedWard();

            needWard.setAppointment(appointment);
            needWard.setRequestTime(LocalDateTime.now());
            //patient.setLastAppointmentId(appointmentId);
            //patientRepository.save(patient);
            needWardRepository.save(needWard);
            return ResponseEntity.ok().body("WardDetails will be shortly assigned to patient.");

        }
        else{
            return ResponseEntity.badRequest().body("wrong details have been put");
        }
    }
}
