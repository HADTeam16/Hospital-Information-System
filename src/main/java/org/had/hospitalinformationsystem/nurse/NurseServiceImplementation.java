package org.had.hospitalinformationsystem.nurse;

import org.had.hospitalinformationsystem.dto.WardPatientDetails;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.needWard.NeedWard;
import org.had.hospitalinformationsystem.needWard.NeedWardRepository;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.ward.Ward;
import org.had.hospitalinformationsystem.ward.WardRepository;
import org.had.hospitalinformationsystem.wardHistory.WardHistory;
import org.had.hospitalinformationsystem.wardHistory.WardHistoryRepository;
import org.had.hospitalinformationsystem.wardHistory.WardHistoryService;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class NurseServiceImplementation implements NurseService {

    @Autowired
    UserRepository userRepository;//
    @Autowired
    PatientRepository patientRepository;//
    @Autowired
    NurseRepository nurseRepository;//
    @Autowired
    NeedWardRepository needWardRepository;//
    @Autowired
    WardRepository wardRepository;//

    @Autowired
    WardHistoryRepository wardHistoryRepository;//
    @Autowired
    WardHistoryService wardHistoryService;//

    @Qualifier("jasyptStringEncryptor")
    @Autowired
    private StringEncryptor stringEncryptor;
    @Override
    public ResponseEntity<?> patientsNeedWard(String jwt) {
        Map<String, String> response = new HashMap<>();
        String role = JwtProvider.getRoleFromJwtToken(jwt);

        String userName = JwtProvider.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUserName(userName);
        Optional<Nurse> nurse = nurseRepository.findById(user.getId());
        if (!role.equals("nurse")) {
            response.put("message", "Wrong user have been provided to see.");
            return ResponseEntity.badRequest().body(response);
        }
        if (nurse.get().isHeadNurse()) {
            List<NeedWard> needWards = needWardRepository.returnNeedWards();
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
    public ResponseEntity<List<Long>> getAllAvailableWardIds(String jwt) {
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        String userName = JwtProvider.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUserName(userName);
        Nurse nurse = nurseRepository.findByUser(user);
        if (role.equals("nurse") && nurse.isHeadNurse()) {
            List<Long> wardIds = wardRepository.findAvailableWardIds();
            return ResponseEntity.ok().body(wardIds);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> assignWard(String jwt, Long wardId, Long needWardId) {
        Map<String, String> response = new HashMap<>();
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        String userName = JwtProvider.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUserName(userName);
        Nurse nurse = nurseRepository.findByUser(user);

        if (role.equals("nurse") && nurse.isHeadNurse()) {
            Optional<Ward> optionalWard = wardRepository.findById(wardId);
            Optional<NeedWard> optionalNeedWard = needWardRepository.findById(needWardId);

            if (optionalWard.isPresent() && optionalNeedWard.isPresent()) {
                WardHistory wardHistory = new WardHistory();
                Ward ward = optionalWard.get();
                NeedWard needWard = optionalNeedWard.get();
                ward.setAppointment(needWard.getAppointment());
                ward.setManagingNurse(nurseRepository.findNurseWithLeastWardsAssigned());
                ward.setPatient(needWard.getAppointment().getPatient());
                ward.setAvailableStatus(false);
                wardRepository.save(ward);
                needWardRepository.deleteById(needWardId);
                wardHistory.setBloodPressure(ward.getAppointment().getBloodPressure());
                wardHistory.setHeartRate(ward.getAppointment().getHeartRate());
                wardHistory.setWeight(ward.getAppointment().getWeight());
                wardHistory.setTemperature(ward.getAppointment().getTemperature());
                wardHistory.setAppointment(ward.getAppointment());
                wardHistory.setLog(LocalDateTime.now());
                wardHistoryRepository.save(wardHistory);
                response.put("message", "assign ward success");
                return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            response.put("message", "Only head nurse can assign ward to patient");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @Override
    public ResponseEntity<Map<String,String>> updateAssignedWardPatientDetails(String jwt, Long wardId, @RequestBody WardPatientDetails wardPatientDetails) {
        Map<String,String>response = new HashMap<>();
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if (role.equals("nurse")) {
            Optional<Ward> ward=wardRepository.findById(wardId);
            if(ward.isPresent()){
                Optional<Patient> patient=patientRepository.findById(ward.get().getPatient().getId());
                if(patient.isPresent()){
                    patient.get().setWeight(wardPatientDetails.getWeight());
                    patient.get().setHeartRate(wardPatientDetails.getHeartRate());
                    patient.get().setBloodPressure(stringEncryptor.encrypt(wardPatientDetails.getBloodPressure()));

                    patient.get().setTemperature(wardPatientDetails.getTemperature());
                    WardHistory wardHistory=new WardHistory();
                    wardHistory.setAppointment(ward.get().getAppointment());
                    wardHistory.setHeartRate(wardPatientDetails.getHeartRate());
                    wardHistory.setWeight(wardPatientDetails.getWeight());
                    wardHistory.setBloodPressure(stringEncryptor.encrypt(wardPatientDetails.getBloodPressure()));

                    wardHistory.setTemperature(wardPatientDetails.getTemperature());
                    wardHistory.setLog(LocalDateTime.now());
                    patientRepository.save(patient.get());
                    wardHistoryRepository.save(wardHistory);
                    response.put("message","Patient details has been updated");
                    return ResponseEntity.ok().body(response);

                }
                else{
                    response.put("message","Patient not found");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            else{
                response.put("message","ward not found");
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.put("message", "Access Denied");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> isNurseIsAHeadNurse(String jwt, Long nurseId) {
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
    public ResponseEntity<List<Ward>> getNurseAllottedWard(String jwt, Long nurseId) {
        List<Ward> wards=wardRepository.allottedWard(nurseId);
        for(Ward w:wards){
            w.getAppointment().setPurpose(stringEncryptor.decrypt(w.getAppointment().getPurpose()));
            w.getAppointment().setBloodPressure(stringEncryptor.decrypt(w.getAppointment().getBloodPressure()));
            w.getPatient().setHeight(stringEncryptor.decrypt((w.getPatient().getHeight())));
            w.getPatient().setBloodGroup(stringEncryptor.decrypt(w.getPatient().getBloodGroup()));
        }
        return ResponseEntity.ok().body(wards);
    }

    @Override
    public List<Patient> getPatientsFromNeedWard(List<NeedWard> needWards) {
        List<Patient> patients = new ArrayList<>();

        for (NeedWard needWard : needWards) {
            patients.add(needWard.getAppointment().getPatient());
        }
        return patients;
    }

    @Override
    public ResponseEntity<List<Patient>> getPatientsFromWard(String jwt) {
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if (!role.equals("nurse")) {
            return ResponseEntity.badRequest().body(null);
        }
        String userName = JwtProvider.getUserNameFromJwtToken(jwt);
        Long nurseId = userRepository.findByUserName(userName).getId();
        List<Patient> patients=wardRepository.assignedPatientsUnderNurse(nurseId);
        for(Patient p:patients){

        }
        return ResponseEntity.ok().body(patients);
    }
}
