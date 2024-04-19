package org.had.hospitalinformationsystem.records;

import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordsServiceImplementation implements RecordsService{

    @Autowired
    RecordsRepository recordsRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Override
    public ResponseEntity<List<Records>> getAllRecordsByAppointmentId(String jwt, Long appointmentId) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("doctor")) {
                List<Records> records = recordsRepository.findRecordsByAppointmentId(appointmentId);
                return ResponseEntity.ok(records);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @Override
    public ResponseEntity<List<Records>> getAllRecordsByPatientId(String jwt, Long patientId) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("doctor")) {
                List<Records> records = recordsRepository.findRecordsByPatientId(patientId);
                return ResponseEntity.ok(records);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
