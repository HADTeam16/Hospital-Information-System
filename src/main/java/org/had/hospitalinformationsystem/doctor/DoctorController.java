package org.had.hospitalinformationsystem.doctor;

import org.had.hospitalinformationsystem.dto.AppointmentFinishDTO;
import org.had.hospitalinformationsystem.ward.Ward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("get/all/wards")
    ResponseEntity<List<Ward>> getAllWards(@RequestHeader("Authorization") String jwt) {
        return doctorService.getAllWards(jwt);
    }
}
