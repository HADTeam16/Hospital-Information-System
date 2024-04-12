package org.had.hospitalinformationsystem.doctor;

import org.had.hospitalinformationsystem.dto.PrescriptionsAndRecords;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface DoctorService {

    List<Doctor> getDoctorsWhoAreSurgeon(List<Doctor> doctors);

    ResponseEntity<?> finishAppointment(String jwt, PrescriptionsAndRecords prescriptionsAndRecords,Long wardFlag);

    Map<String,Long> getSpecialityWiseDoctorsCount();

    public Map<String, Long> getCurrentlyAvailableSpecialityWiseDoctorsCount();

}
