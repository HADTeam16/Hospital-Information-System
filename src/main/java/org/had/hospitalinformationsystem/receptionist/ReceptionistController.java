package org.had.hospitalinformationsystem.receptionist;


import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/receptionist")
public class ReceptionistController {
    @Autowired
    ReceptionistService receptionistService;

    @PostMapping("/signup/patient")
    public ResponseEntity<Object> signupPatient(@RequestHeader("Authorization") String jwt, @RequestBody RegistrationDto registrationDto) {
        return receptionistService.signupPatient(jwt, registrationDto);
    }

    @GetMapping("/find/doctor/by/specialization/{specialization}")
    public ResponseEntity<?> findDoctorBySpecialization(@RequestHeader("Authorization") String jwt, @PathVariable String specialization) {
        return findDoctorBySpecialization(jwt, specialization);
    }

    @GetMapping("/create/ward")
    public ResponseEntity<String> createWard(@RequestHeader("Authorization") String jwt){
        return receptionistService.createWard(jwt);
    }

    @GetMapping("/get/all/receptionist")
    public ResponseEntity<List<Receptionist>> getAllReceptionist(@RequestHeader("Authorization") String jwt) {
        return receptionistService.getAllReceptionist(jwt);
    }
    @GetMapping("/normal/{userid}")
    public Boolean haveConsent(@RequestHeader("Authorization") String jwt,@PathVariable Long userid){
        return receptionistService.checkPatientByPatientId(jwt,userid);
    }
    @GetMapping("/send/otp/for/delete/patient/data/request/{id}")
    public ResponseEntity<Map<String,String>>deletePatientsendOtp(@RequestHeader("Authorization") String jwt,@PathVariable Long id){
        return receptionistService.deletePatientsendOtp(jwt,id);
    }

    @GetMapping("/validate/otp/for/delete/patient/data/request/{id}/{email}/{otp}")
    public ResponseEntity<Map<String,String>>deletePatientDataValidateOtp(@RequestHeader("Authorization") String jwt,@PathVariable Long id, @PathVariable String email, @PathVariable String otp){
        return receptionistService.deletePatientDataValidateOtp(jwt,id,email,otp);
    }

}
