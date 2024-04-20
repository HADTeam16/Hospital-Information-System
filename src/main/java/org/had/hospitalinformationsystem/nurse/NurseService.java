package org.had.hospitalinformationsystem.nurse;

import org.had.hospitalinformationsystem.dto.WardPatientDetails;
import org.had.hospitalinformationsystem.needWard.NeedWard;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.ward.Ward;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


public interface NurseService {

    ResponseEntity<?>  patientsNeedWard(String jwt);

    ResponseEntity<List<Nurse>> getAllNurse(String jwt);

    ResponseEntity<List<Ward>> getAllAvailableWards(String jwt);

    ResponseEntity<List<Long>> getAllAvailableWardIds(String jwt);

    ResponseEntity<Map<String,String>> assignWard(String jwt, Long wardId, Long needWardId);

    ResponseEntity<Map<String,String>> updateAssignedWardPatientDetails(String jwt, Long wardId, @RequestBody WardPatientDetails wardPatientDetails);

    ResponseEntity<Map<String, String>> isNurseIsAHeadNurse(String jwt,Long nurseId);

    ResponseEntity<List<Ward>> getNurseAllottedWard(String jwt,Long nurseId);

    List<Patient> getPatientsFromNeedWard(List<NeedWard> needWards);

    ResponseEntity<List<Patient>> getPatientsFromWard(String jwt);
}
