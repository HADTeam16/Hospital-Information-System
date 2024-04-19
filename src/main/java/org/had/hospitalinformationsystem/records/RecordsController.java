package org.had.hospitalinformationsystem.records;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class RecordsController {

    @Autowired
    RecordsService recordsService;

    @GetMapping("/get/records/by/appointment/{appointmentId}")
    public ResponseEntity<List<Records>> getAllRecordsByAppointmentId(@RequestHeader("Authorization") String jwt, @PathVariable Long appointmentId) {
        return recordsService.getAllRecordsByAppointmentId(jwt, appointmentId);
    }

    @GetMapping("/get/records/by/patient/{patientId}")
    public ResponseEntity<List<Records>> getAllRecordsByPatientId(@RequestHeader("Authorization") String jwt, @PathVariable Long patientId) {
        return recordsService.getAllRecordsByPatientId(jwt, patientId);
    }

}
