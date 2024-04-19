package org.had.hospitalinformationsystem.nurse;

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
import org.had.hospitalinformationsystem.ward.WardService;
import org.had.hospitalinformationsystem.wardHistory.WardHistory;
import org.had.hospitalinformationsystem.wardHistory.WardHistoryRepository;
import org.had.hospitalinformationsystem.wardHistory.WardHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class NurseServiceImplementation implements NurseService{

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
    @Autowired
    WardService wardService;
    @Autowired
    WardHistoryRepository wardHistoryRepository;
    @Autowired
    WardHistoryService wardHistoryService;


    @Override
    public ResponseEntity<?>  patientsNeedWard(String jwt){
        Map<String,String>response = new HashMap<>();
        String role= JwtProvider.getRoleFromJwtToken(jwt);

        String userName=JwtProvider.getUserNameFromJwtToken(jwt);
        User user=userRepository.findByUserName(userName);
        Optional<Nurse> nurse=nurseRepository.findById(user.getId());
        if(!role.equals("nurse")){
            response.put("message","Wrong user have been provided to see.");
            return ResponseEntity.badRequest().body(response);
        }
        if (nurse.get().isHeadNurse()) {
            List<NeedWard> needWards = needWardRepository.returnNeedWards();
            List<Patient> patients = nurseService.getPatientsFromNeedWard(needWards);
            return ResponseEntity.ok(needWards);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @Override
    public ResponseEntity<List<Nurse>> getAllNurse(String jwt) {
        String userName = JwtProvider.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUserName(userName);
        if (user.getRole().equals("admin")) {
            List<Nurse> nurses = nurseRepository.findAll();
            return ResponseEntity.ok().body(nurses);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Override
    public ResponseEntity<List<Ward>> getAllAvailableWards(String jwt) {
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        String userName = JwtProvider.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUserName(userName);
        Nurse nurse = nurseRepository.findByUser(user);
        if (role.equals("nurse") && nurse.isHeadNurse()) {
            List<Ward> wards = wardRepository.findAvailableWard();
            return ResponseEntity.ok().body(wards);
        } else {
            return ResponseEntity.badRequest().body(null);
        }

    }

    @Override
    public ResponseEntity<List<Long>> getAllAvailableWardIds(String jwt){
        String role=JwtProvider.getRoleFromJwtToken(jwt);
        String userName=JwtProvider.getUserNameFromJwtToken(jwt);
        User user=userRepository.findByUserName(userName);
        Nurse nurse=nurseRepository.findByUser(user);
        if(role.equals("nurse") && nurse.isHeadNurse()){
            List<Long> wardIds=wardRepository.findAvailableWardIds();
            return ResponseEntity.ok().body(wardIds);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Override
    public ResponseEntity<?> assignWard(String jwt, Long wardId, Long needWardId) {
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        String userName = JwtProvider.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUserName(userName);
        Nurse nurse = nurseRepository.findByUser(user);

        if (role.equals("nurse") && nurse.isHeadNurse()) {
            Optional<Ward> optionalWard = wardRepository.findById(wardId);
            Optional<NeedWard> optionalNeedWard = needWardRepository.findById(needWardId);

            if (optionalWard.isPresent() && optionalNeedWard.isPresent()) {
                WardHistory wardHistory=new WardHistory();
                Ward ward = optionalWard.get();
                NeedWard needWard = optionalNeedWard.get();
                ward.setAppointment(needWard.getAppointment());
                ward.setManagingNurse(nurseRepository.findNurseWithLeastWardsAssigned());
                ward.setPatient(needWard.getAppointment().getPatient());
                ward.setAvailableStatus(false);

                Ward updatedWard = wardRepository.save(ward);
                needWardRepository.deleteById(needWardId);
                wardHistory.setBloodPressure(ward.getAppointment().getBloodPressure());
                wardHistory.setHeight(ward.getAppointment().getHeight());
                wardHistory.setWeight(ward.getAppointment().getWeight());
                wardHistory.setTemperature(ward.getAppointment().getTemperature());
                wardHistory.setAppointment(ward.getAppointment());
                wardHistory.setLog(LocalDateTime.now());

                wardHistoryRepository.save(wardHistory);
                return ResponseEntity.ok().body(updatedWard);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            Map<String,String> response=new HashMap<>();
            response.put("message","Only head nurse can assign ward to patient");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @Override
    public ResponseEntity<Map<String,String>> updateAssignedWardPatientDetails(String jwt, Long patientId, @RequestBody WardPatientDetails wardPatientDetails) {
        Map<String,String>response = new HashMap<>();
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if (role.equals("nurse")) {
            Optional<Patient> patientO = patientRepository.findById(patientId);
            if (patientO.isPresent()) {
                Patient patient = patientO.get();
                patient.setTemperature(wardPatientDetails.getTemperature());
                patient.setBloodPressure(wardPatientDetails.getBloodPressure());
                patient.setWeight(wardPatientDetails.getWeight());
                patientRepository.save(patient);
                response.put("message","Updated");
                Ward ward=wardRepository.findByPatient(patientId);
                WardHistory wardHistory=new WardHistory();
                wardHistory.setTemperature(wardPatientDetails.getTemperature());
                wardHistory.setBloodPressure(wardPatientDetails.getBloodPressure());
                wardHistory.setWeight(wardPatientDetails.getWeight());
                wardHistory.setLog(LocalDateTime.now());
                wardHistory.setAppointment(ward.getAppointment());
                wardHistoryRepository.save(wardHistory);
                return ResponseEntity.ok(response);
            } else {
                response.put("message","Failed");
                return ResponseEntity.ok(response);
            }
        } else {
            response.put("message","Access Denied");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> isNurseIsAHeadNurse(String jwt,Long nurseId) {
        Map<String, String> response = new HashMap<>();
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("nurse")) {
                Optional<Nurse> isNurse = nurseRepository.findById(nurseId);
                if (isNurse.isPresent()) {
                    Nurse nurse = isNurse.get();
                    boolean isHeadNurse = nurse.isHeadNurse();
                    if (isHeadNurse) {
                        response.put("message", "yes");
                        return ResponseEntity.ok(response);
                    } else {
                        response.put("message", "no");
                        return ResponseEntity.ok(response);
                    }
                } else {
                    response.put("message", "yes");
                    return ResponseEntity.ok(response);
                }
            } else {
                response.put("message", "Access Denied");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Override
    public ResponseEntity<List<Ward>> getNurseAllottedWard(String jwt,Long nurseId){
        return ResponseEntity.ok().body(wardRepository.allottedWard(nurseId));
    }

    @Override
    public List<Patient> getPatientsFromNeedWard(List<NeedWard> needWards) {
        List<Patient> patients=new ArrayList<>();
        
        for(NeedWard needWard:needWards){
            patients.add(needWard.getAppointment().getPatient());
        }
        return patients;
    }

    @Override
    public ResponseEntity<List<Patient>> getPatientsFromWard(String jwt) {
        String role= JwtProvider.getRoleFromJwtToken(jwt);
        if(!role.equals("nurse")){
            return ResponseEntity.badRequest().body(null);
        }
        String userName=JwtProvider.getUserNameFromJwtToken(jwt);
        Long nurseId=userRepository.findByUserName(userName).getId();
        return ResponseEntity.ok().body(wardRepository.assignedPatientsUnderNurse(nurseId));
    }
}
