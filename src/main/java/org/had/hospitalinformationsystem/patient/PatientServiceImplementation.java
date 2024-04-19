package org.had.hospitalinformationsystem.patient;

import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImplementation implements PatientService{
    @Autowired
    PatientRepository patientRepository;

    @Override
    public ResponseEntity<?> getAllPatient(String jwt) {
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
