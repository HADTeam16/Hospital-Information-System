package org.had.hospitalinformationsystem.prescription;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface PrescriptionService {

    ResponseEntity<Map<String,String>> addPrescription(String jwt, String prescription, Long appointmentId);

    ResponseEntity<Prescription> getPrescriptionFromAppointment(String jwt,Long appointmentId);
}
