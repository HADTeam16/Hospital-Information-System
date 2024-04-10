package org.had.hospitalinformationsystem.nurse;

import org.apache.coyote.Response;
import org.had.hospitalinformationsystem.dto.WardPatientDetails;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.needWard.NeedWard;
import org.had.hospitalinformationsystem.needWard.NeedWardRepository;
import org.had.hospitalinformationsystem.needWard.NeedWardService;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.ward.Ward;
import org.had.hospitalinformationsystem.ward.WardRepository;
import org.hibernate.sql.ast.tree.AbstractUpdateOrDeleteStatement;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    WardRepository wardRepository;
    @Autowired
    NeedWardService needWardService;

    //this api will help you find patients in needWard table
    @GetMapping("patients/who/needs/ward")
    public ResponseEntity<?>  patientsNeedWard(@RequestHeader("Authorization") String jwt){
        String role= JwtProvider.getRoleFromJwtToken(jwt);

        String userName=JwtProvider.getUserNameFromJwtToken(jwt);
        User user=userRepository.findByUserName(userName);
        Optional<Nurse> nurse=nurseRepository.findById(user.getId());
        if(!role.equals("nurse")){
            return ResponseEntity.badRequest().body("Wrong user have been provided to see.");

        }
        if(nurse.get().isHeadNurse()){
            List<NeedWard> needWards=needWardRepository.returnNeedWards();
            List<Patient> patients=nurseService.getPatientsFromNeedWard(needWards);
            return ResponseEntity.ok(needWards);
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
    @GetMapping("get/all/available/wards")
    ResponseEntity<List<Ward>> getAllAvailableWards(@RequestHeader("Authorization") String jwt){
        String role=JwtProvider.getRoleFromJwtToken(jwt);
        String userName=JwtProvider.getUserNameFromJwtToken(jwt);
        User user=userRepository.findByUserName(userName);
        Nurse nurse=nurseRepository.findByUser(user);
        if(role.equals("nurse") && nurse.isHeadNurse()){
            List<Ward> wards=wardRepository.findAvailableWard();
            return ResponseEntity.ok().body(wards);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }

    }

    @GetMapping("/assign/ward/{wardId}/{needWardId}")
    ResponseEntity<?> assignWard(@RequestHeader("Authorization") String jwt, @PathVariable Long wardId, @PathVariable Long needWardId) {
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        String userName = JwtProvider.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUserName(userName);
        Nurse nurse = nurseRepository.findByUser(user);

        if (role.equals("nurse") && nurse.isHeadNurse()) {
            Optional<Ward> optionalWard = wardRepository.findById(wardId);
            Optional<NeedWard> optionalNeedWard = needWardRepository.findById(needWardId);

            if (optionalWard.isPresent() && optionalNeedWard.isPresent()) {
                Ward ward = optionalWard.get();
                NeedWard needWard = optionalNeedWard.get();

                ward.setAppointment(needWard.getAppointment());
                ward.setManagingNurse(nurse);
                ward.setPatient(needWard.getAppointment().getPatient());
                ward.setAvailableStatus(false);

                Ward updatedWard = wardRepository.save(ward);
                needWardRepository.deleteById(needWardId);

                return ResponseEntity.ok().body(updatedWard);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only head nurse can assign ward to patient");
        }
    }

    @PutMapping("/update/assigned/ward/patient/details/{wardId}")
    ResponseEntity<String>updateAssignedWardPatientDetails(@RequestHeader("Authorization") String jwt, @PathVariable Long wardId, @RequestBody WardPatientDetails wardPatientDetails){
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if(role.equals("nurse")){
            Optional<Patient> patientO = patientRepository.findById(wardRepository.findById(wardId).get().getPatient().getId());
            if(patientO.isPresent()){
                Patient patient = patientO.get();
                patient.setTemperature(wardPatientDetails.getTemperature());
                patient.setBloodPressure(wardPatientDetails.getBloodPressure());
                patient.setWeight(wardPatientDetails.getWeight());
                patientRepository.save(patient);
                return ResponseEntity.ok("Updated");
            }
            else{
                return ResponseEntity.ok("Failed");
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied");
        }
    }

    @GetMapping("/is/head/nurse/{nurseId}")
    ResponseEntity<String>isNurseIsAHeadNurse(@RequestHeader("Authorization")String jwt,@PathVariable Long nurseId){
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("nurse")) {
                Optional<Nurse> isNurse = nurseRepository.findById(nurseId);
                if(isNurse.isPresent()){
                    Nurse nurse = isNurse.get();
                    boolean isHeadNurse = nurse.isHeadNurse();
                    if(isHeadNurse){
                        return ResponseEntity.ok("yes");
                    }
                    else{
                        return ResponseEntity.ok("no");
                    }
                }
                else{
                    return ResponseEntity.ok("No nurse present with the given Id");
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied");
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: "+e.getMessage());
        }
    }
    @GetMapping("/allotted/ward/{nurseId}")
    ResponseEntity<List<Ward>> getNurseAllottedWard(@RequestHeader("Authorization")String jwt,@PathVariable Long nurseId){
        return ResponseEntity.ok().body(wardRepository.allottedWard(nurseId));
    }

}
