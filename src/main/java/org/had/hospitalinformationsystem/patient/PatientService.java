package org.had.hospitalinformationsystem.patient;

import org.springframework.http.ResponseEntity;

public interface PatientService {

    ResponseEntity<?> getAllPatient(String jwt);
}
