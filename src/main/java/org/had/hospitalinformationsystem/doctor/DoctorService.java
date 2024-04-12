package org.had.hospitalinformationsystem.doctor;

import org.had.hospitalinformationsystem.dto.AppointmentFinishDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface DoctorService {

    List<Doctor> getDoctorsWhoAreSurgeon(List<Doctor> doctors);

    ResponseEntity<Map<String, String>> finishAppointment(String jwt, AppointmentFinishDTO prescriptionsAndRecords);

    Map<String,Long> getSpecialityWiseDoctorsCount();

    public Map<String, Long> getCurrentlyAvailableSpecialityWiseDoctorsCount();

}
