package org.had.hospitalinformationsystem.doctor;

import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.dto.PrescriptionsAndRecords;
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
    @Autowired
    DoctorService doctorService;

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
    public ResponseEntity<Map<String, String>> assignWard(@RequestHeader("Authorization") String jwt,
                                                          @PathVariable long appointmentId) {
        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId);
        NeedWard needWard = new NeedWard();
        needWard.setAppointment(appointment);
        needWard.setRequestTime(LocalDateTime.now());

        needWardRepository.save(needWard);
        Map<String, String> response = new HashMap<>();
        response.put("message", "success");
        return ResponseEntity.ok(response);
    }
    @PostMapping("/finish/appointment/{wardFlag}")
    public ResponseEntity<?> finishAppointment(@RequestHeader("Authorization") String jwt,
                                               @RequestBody PrescriptionsAndRecords prescriptionsAndRecords,
                                               @PathVariable long wardFlag){
        return doctorService.finishAppointment(jwt,prescriptionsAndRecords,wardFlag);

    }
}
