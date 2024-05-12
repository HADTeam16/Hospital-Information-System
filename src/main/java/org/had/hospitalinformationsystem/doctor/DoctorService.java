package org.had.hospitalinformationsystem.doctor;

import org.had.hospitalinformationsystem.dto.AppointmentFinishDTO;
import org.had.hospitalinformationsystem.ward.Ward;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface DoctorService {

    ResponseEntity<?> getAllDoctor(String jwt);

    ResponseEntity<Map<String, String>> assignWard(String jwt, long appointmentId);

    List<Doctor> getDoctorsWhoAreSurgeon(List<Doctor> doctors);

    ResponseEntity<Map<String, String>> finishAppointment(String jwt, AppointmentFinishDTO prescriptionsAndRecords);

    Map<String,Long> getSpecialityWiseDoctorsCount();

    public Map<String, Long> getCurrentlyAvailableSpecialityWiseDoctorsCount();

    ResponseEntity<List<Ward>> getAllWards(String jwt);

}
