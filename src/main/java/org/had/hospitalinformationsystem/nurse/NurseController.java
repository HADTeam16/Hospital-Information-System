package org.had.hospitalinformationsystem.nurse;

import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/nurse")
public class NurseController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    NurseRepository nurseRepository;

    @GetMapping("patients/who/needs/ward")
    public ResponseEntity<List<Patient>>  patientsNeedWard(@RequestHeader("Authorization") String jwt){
        String role= JwtProvider.getRoleFromJwtToken(jwt);
        String userName=JwtProvider.getUserNameFromJwtToken(jwt);
        User user=userRepository.findByUserName(userName);
        Optional<Nurse> nurse=nurseRepository.findById(user.getId());

        if(nurse.get().isHeadNurse()){

            return ResponseEntity.ok(patientRepository.findPatientWhoNeedsWard());
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
//    @GetMapping("/assign/ward/{patientId}")
//    public ResponseEntity<String> assignWard(@RequestHeader("Authorization") String jwt, @PathVariable Long PatiendId){
//
//    }
}
