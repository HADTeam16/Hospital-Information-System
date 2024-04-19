package org.had.hospitalinformationsystem.prescription;

import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PrescriptionServiceImplementation implements PrescriptionService{

    @Autowired
    PrescriptionRepository prescriptionRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Override
    public ResponseEntity<Map<String,String>> addPrescription(String jwt, String prescription, Long appointmentId){
        Map<String,String> response = new HashMap<>();
        response.put("message", "unknown error");
        try{
            String role= JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("doctor")){
                Prescription prescription1=new Prescription();
                prescription1.setPrescription(prescription);
                Appointment appointment=appointmentRepository.findByAppointmentId(appointmentId);
                prescription1.setAppointment(appointment);
                prescriptionRepository.save(prescription1);
                response.put("message", "success");
                return ResponseEntity.ok(response);
            }
            else{
                response.put("message", "unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }
        catch(Exception e){
            response.put("message", "unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

    }

    @Override
    public ResponseEntity<Prescription> getPrescriptionFromAppointment(String jwt,Long appointmentId) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("doctor")) {
                Prescription prescription = prescriptionRepository.findPrescriptionByAppointmentID(appointmentId);
                return ResponseEntity.ok(prescription);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
