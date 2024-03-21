package org.had.hospitalinformationsystem.nurse;

import org.had.hospitalinformationsystem.needWard.NeedWard;
import org.had.hospitalinformationsystem.patient.Patient;
import org.springframework.stereotype.Service;

import java.util.List;


public interface NurseService {
    public List<Patient> getPatientsFromNeedWard(List<NeedWard> needWards);
}
