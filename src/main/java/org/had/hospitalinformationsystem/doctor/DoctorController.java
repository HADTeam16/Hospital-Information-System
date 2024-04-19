package org.had.hospitalinformationsystem.doctor;

import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.dto.AppointmentFinishDTO;
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
    DoctorService doctorService;

    @GetMapping("/get/all/doctors")
    public ResponseEntity<?> getAllDoctor(@RequestHeader("Authorization") String jwt) {
        return doctorService.getAllDoctor(jwt);
    }

    @GetMapping("/recommend/ward/{appointmentId}")
    public ResponseEntity<Map<String, String>> assignWard(@RequestHeader("Authorization") String jwt, @PathVariable long appointmentId) {
        return doctorService.assignWard(jwt, appointmentId);
    }

    @PostMapping("/finish/appointment")
    public ResponseEntity<Map<String, String>> finishAppointment(@RequestHeader("Authorization") String jwt, @RequestBody AppointmentFinishDTO prescriptionsAndRecords) {
        return doctorService.finishAppointment(jwt, prescriptionsAndRecords);
    }
}
