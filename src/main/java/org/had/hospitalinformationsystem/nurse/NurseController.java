package org.had.hospitalinformationsystem.nurse;

import org.apache.coyote.Response;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.needWard.NeedWard;
import org.had.hospitalinformationsystem.needWard.NeedWardRepository;
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

    @Autowired
    NurseService nurseService;

    @Autowired
    NeedWardRepository needWardRepository;
    @GetMapping("patients/who/needs/ward")
    public ResponseEntity<List<Patient>>  patientsNeedWard(@RequestHeader("Authorization") String jwt){
        String role= JwtProvider.getRoleFromJwtToken(jwt);

        String userName=JwtProvider.getUserNameFromJwtToken(jwt);
        User user=userRepository.findByUserName(userName);
        Optional<Nurse> nurse=nurseRepository.findById(user.getId());
        if(!role.equals("nurse")){
            return ResponseEntity.badRequest().body(null);

        }
        if(nurse.get().isHeadNurse()){
            List<NeedWard> needWards=needWardRepository.returnNeedWards();
            List<Patient> patients=nurseService.getPatientsFromNeedWard(needWards);
            return ResponseEntity.ok(patients);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/get/all/nurse")
    public ResponseEntity<List<Nurse>> getAllNurse(@RequestHeader("Authorization") String jwt){
        String userName=JwtProvider.getUserNameFromJwtToken(jwt);
        User user=userRepository.findByUserName(userName);
        if(user.getRole().equals("admin")){
            List<Nurse> nurses=nurseRepository.findAll();
            return ResponseEntity.ok().body(nurses);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

}
