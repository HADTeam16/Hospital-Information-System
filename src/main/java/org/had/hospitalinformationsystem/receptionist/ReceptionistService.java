package org.had.hospitalinformationsystem.receptionist;

import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ReceptionistService {

    void sendAppointmentUpdate(Appointment appointment);

    ResponseEntity<Object> signupPatient(String jwt, RegistrationDto registrationDto);

    ResponseEntity<?> findDoctorBySpecialization(String jwt, String specialization);

    ResponseEntity<String> createWard(String jwt);

    ResponseEntity<List<Receptionist>> getAllReceptionist(String jwt);
    public  Boolean checkPatientByPatientId(String jwt,Long id);
    ResponseEntity<Map<String,String>> deletePatientsendOtp(String jwt, Long id);

    ResponseEntity<Map<String, String>> deletePatientDataValidateOtp(String jwt,Long id, String email, String otp);
}
