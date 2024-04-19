package org.had.hospitalinformationsystem.prescription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    @Autowired
    PrescriptionService prescriptionService;

    @PostMapping("/add/prescription/{appointmentId}")
    public ResponseEntity<Map<String, String>> addPrescription(@RequestHeader("Authorization") String jwt, @RequestBody String prescription, @PathVariable Long appointmentId) {
        return prescriptionService.addPrescription(jwt, prescription, appointmentId);
    }

    @GetMapping("/get/prescription/from/appointment")
    public ResponseEntity<Prescription> getPrescriptionFromAppointment(@RequestHeader("Authorization") String jwt, @RequestParam Long appointmentId) {
        return prescriptionService.getPrescriptionFromAppointment(jwt, appointmentId);
    }
}
