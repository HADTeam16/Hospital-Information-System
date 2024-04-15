package org.had.hospitalinformationsystem.nurse;

import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.needWard.NeedWard;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.ward.WardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NurseServiceImplementation implements NurseService{

    @Autowired
    UserRepository userRepository;
    @Autowired
    WardRepository wardRepository;
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
