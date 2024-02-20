package org.had.hospitalinformationsystem.patient;

import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    PatientRepository patientRepository;

    @GetMapping("/getallpatients")
    public ResponseEntity<?> getAllPatient(@RequestHeader("Authorization") String jwt) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (!role.equals("admin") && !role.equals("receptionist")) {
                throw new Exception("Access Denied!!!");
            }
            List<Patient> allPatient = patientRepository.findAll();
            return ResponseEntity.ok(allPatient);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving patients: " + e.getMessage());
        }
    }
}
