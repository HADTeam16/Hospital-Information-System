package org.had.hospitalinformationsystem.nurse;

import org.had.hospitalinformationsystem.dto.WardPatientDetails;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.ward.Ward;
import org.had.hospitalinformationsystem.ward.WardService;
import org.had.hospitalinformationsystem.wardHistory.WardHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nurse")
public class NurseController {

    @Autowired
    NurseService nurseService;
    @Autowired
    WardService wardService;
    @Autowired
    WardHistoryService wardHistoryService;

    @GetMapping("/patients/who/needs/ward")
    public ResponseEntity<?>  patientsNeedWard(@RequestHeader("Authorization") String jwt){
        return nurseService.patientsNeedWard(jwt);
    }

    @GetMapping("/get/all/nurse")
    public ResponseEntity<List<Nurse>> getAllNurse(@RequestHeader("Authorization") String jwt) {
        return nurseService.getAllNurse(jwt);
    }

    @GetMapping("/get/all/available/wardIds")
    ResponseEntity<List<Long>> getAllAvailableWardIds(@RequestHeader("Authorization") String jwt){
        return nurseService.getAllAvailableWardIds(jwt);
    }

    @GetMapping("/assign/ward/{wardId}/{needWardId}")
    ResponseEntity<Map<String,String>> assignWard(@RequestHeader("Authorization") String jwt, @PathVariable Long wardId, @PathVariable Long needWardId) {
        return nurseService.assignWard(jwt, wardId, needWardId);
    }

    @PutMapping("/update/assigned/ward/patient/details/{wardId}")
    ResponseEntity<Map<String,String>> updateAssignedWardPatientDetails(@RequestHeader("Authorization") String jwt, @PathVariable Long wardId, @RequestBody WardPatientDetails wardPatientDetails) {
        return nurseService.updateAssignedWardPatientDetails(jwt, wardId, wardPatientDetails);
    }

    @GetMapping("/is/head/nurse/{nurseId}")
    ResponseEntity<Map<String, String>> isNurseIsAHeadNurse(@RequestHeader("Authorization") String jwt, @PathVariable Long nurseId) {
        return  nurseService.isNurseIsAHeadNurse(jwt, nurseId);
    }

    @GetMapping("/allotted/ward/{nurseId}")
    ResponseEntity<List<Ward>> getNurseAllottedWard(@RequestHeader("Authorization")String jwt,@PathVariable Long nurseId){
        return nurseService.getNurseAllottedWard(jwt, nurseId);
    }

    @GetMapping("/assigned/patients")
    ResponseEntity<List<Patient>> getAssignedPatients(@RequestHeader("Authorization")String jwt){
        return nurseService.getPatientsFromWard(jwt);
    }

    @GetMapping("/call/emergency/{wardId}")
    ResponseEntity<?>callEmergency(@RequestHeader("Authorization")String jwt, @PathVariable Long wardId){
        return wardService.callEmergency(jwt,wardId);
    }

    @GetMapping("/ward/history/{wardId}")
    ResponseEntity<?> wardHistory(@RequestHeader("Authorization") String jwt,@PathVariable Long wardId){
            return wardHistoryService.getWardHistory(jwt,wardId);
        }
}
