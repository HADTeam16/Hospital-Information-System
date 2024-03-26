package org.had.hospitalinformationsystem.records;

import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class RecordsController {

    @Autowired
    RecordsRepository recordsRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @PostMapping("/add/records/{appointmentId}")
    public ResponseEntity<Records>uploadRecords(@RequestHeader("Authorization") String jwt, @PathVariable Long appointmentId, @RequestBody String recordImage){
        try{
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("doctor")){
                Appointment currAppointment = appointmentRepository.findByAppointmentId(appointmentId);
                if(currAppointment != null){
                    Records newRecord = new Records();
                    newRecord.setRecordImage(recordImage);
                    newRecord.setAppointment(currAppointment);
                    recordsRepository.save(newRecord);
                    return ResponseEntity.ok(newRecord);
                }
                else{
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/get/records/by/appointment/{appointmentId}")
    public ResponseEntity<List<Records>> getAllRecordsByAppointmentId(@RequestHeader("Authorization") String jwt, @PathVariable Long appointmentId) {
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

    @GetMapping("/get/records/by/patient/{patientId}")
    public ResponseEntity<List<Records>>getAllRecordsByPatientId(@RequestHeader("Authorization") String jwt, @PathVariable Long patientId){
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
