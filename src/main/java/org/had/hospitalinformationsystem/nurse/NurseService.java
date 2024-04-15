package org.had.hospitalinformationsystem.nurse;

import org.had.hospitalinformationsystem.needWard.NeedWard;
import org.had.hospitalinformationsystem.patient.Patient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


public interface NurseService {
    List<Patient> getPatientsFromNeedWard(List<NeedWard> needWards);

    ResponseEntity<List<Patient>> getPatientsFromWard(String jwt);
}
