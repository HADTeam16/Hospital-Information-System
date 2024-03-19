package org.had.hospitalinformationsystem.doctor;


import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    AppointmentRepository appointmentRepository;


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
    public String assignWard(@RequestHeader("Authorization") String jwt,@PathVariable long appointmentId){
        Appointment appointment=appointmentRepository.findByAppointmentId(appointmentId);
        Patient patient=appointment.getPatient();
        patient.setNeedWard(true);
        patient.setLastAppointmentId(appointmentId);
        patientRepository.save(patient);
        return "Ward will be shortly assigned to patient.";
    }
}
