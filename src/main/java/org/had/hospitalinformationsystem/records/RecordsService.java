package org.had.hospitalinformationsystem.records;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RecordsService {
    ResponseEntity<List<Records>> getAllRecordsByAppointmentId(String jwt, Long appointmentId);

    ResponseEntity<List<Records>> getAllRecordsByPatientId(String jwt, Long patientId);
}
