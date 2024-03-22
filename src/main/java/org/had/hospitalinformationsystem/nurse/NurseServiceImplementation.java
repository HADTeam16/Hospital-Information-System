package org.had.hospitalinformationsystem.nurse;

import org.had.hospitalinformationsystem.needWard.NeedWard;
import org.had.hospitalinformationsystem.patient.Patient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NurseServiceImplementation implements NurseService{


    @Override
    public List<Patient> getPatientsFromNeedWard(List<NeedWard> needWards) {
        List<Patient> patients=new ArrayList<>();
        
        for(NeedWard needWard:needWards){
            patients.add(needWard.getAppointment().getPatient());
        }
        return patients;
    }
}
