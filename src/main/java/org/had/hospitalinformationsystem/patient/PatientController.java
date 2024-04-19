package org.had.hospitalinformationsystem.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    PatientService patientService;

    @GetMapping("/getallpatients")
    public ResponseEntity<?> getAllPatient(@RequestHeader("Authorization") String jwt) {
        return patientService.getAllPatient(jwt);
    }
}
