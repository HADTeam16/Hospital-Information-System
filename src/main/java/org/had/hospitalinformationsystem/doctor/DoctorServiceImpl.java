package org.had.hospitalinformationsystem.doctor;

import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.dto.AppointmentFinishDTO;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.needWard.NeedWard;
import org.had.hospitalinformationsystem.needWard.NeedWardRepository;
import org.had.hospitalinformationsystem.prescription.Prescription;
import org.had.hospitalinformationsystem.prescription.PrescriptionRepository;
import org.had.hospitalinformationsystem.records.Records;
import org.had.hospitalinformationsystem.records.RecordsRepository;
import org.had.hospitalinformationsystem.ward.Ward;
import org.had.hospitalinformationsystem.ward.WardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class DoctorServiceImpl implements DoctorService {
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    NeedWardRepository needWardRepository;
    @Autowired
    PrescriptionRepository prescriptionRepository;
    @Autowired
    RecordsRepository recordsRepository;
    @Autowired
    WardRepository wardRepository;//

    @Override
    public ResponseEntity<?> getAllDoctor(String jwt) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (!role.equals("admin") && !role.equals("receptionist")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Unauthorized role");
            }
            List<Doctor> allDoctor = doctorRepository.findAll();
            if (allDoctor.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(allDoctor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve doctors: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> assignWard(String jwt, long appointmentId) {
        Map<String, String> response = new HashMap<>();
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(!role.equals("nurse")){
                response.put("message", "Access denied");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId);
            NeedWard needWard = new NeedWard();
            needWard.setAppointment(appointment);
            needWard.setRequestTime(LocalDateTime.now());

            needWardRepository.save(needWard);

            response.put("message", "success");
            return ResponseEntity.ok(response);
        }
        catch(Exception e){
            response.put("message","error: "+e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Override
    public List<Doctor> getDoctorsWhoAreSurgeon(List<Doctor> doctors) {
        List<Doctor> surgeons = new ArrayList<>();
        for (Doctor doctor : doctors) {
            if (doctor.getSpecialization().toLowerCase().contains("surgeon")) {
                surgeons.add(doctor);
            }
        }
        return surgeons;
    }

    @Override
    public ResponseEntity<Map<String, String>> finishAppointment(String jwt,
            AppointmentFinishDTO prescriptionsAndRecords) {
        Map<String, String> response = new HashMap<>();
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (!role.equals("doctor")) {
                response.put("message", "Only doctor can finish appointment");
                return ResponseEntity.badRequest().body(response);
            }
            Appointment appointment = appointmentRepository
                    .findByAppointmentId(prescriptionsAndRecords.getAppointmentId());
            if (appointment == null) {
                response.put("message", "No appointment found");
                return ResponseEntity.badRequest().body(response);
            }

            if (prescriptionsAndRecords.getNeedWard()) {
                NeedWard needWard = new NeedWard();
                needWard.setAppointment(appointment);
                needWard.setRequestTime(LocalDateTime.now());
                appointment.setNeedWard(true);
                needWardRepository.save(needWard);
            }

            Prescription prescription = new Prescription();
            prescription.setPrescription(prescriptionsAndRecords.getPrescription());

            for (String recordImage : prescriptionsAndRecords.getRecords()) {
                Records newRecord = new Records();
                newRecord.setRecordImage(recordImage);
                newRecord.setAppointment(appointment);
                recordsRepository.save(newRecord);
            }
            appointment.setCompleted(1);
            prescription.setAppointment(appointment);
            prescriptionRepository.save(prescription);
            System.out.println(appointment.getCompleted());
            appointmentRepository.save(appointment);

            response.put("message", "Appointment finished successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {

            System.err.println("An error occurred in finishAppointment: " + e.getMessage());
            response.put("message", "An error occurred while finishing the appointment.\n " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public Map<String, Long> getSpecialityWiseDoctorsCount() {
        List<Object[]> results = doctorRepository.countDoctorsBySpecialization();

        Map<String, Long> specialityWiseCount = new HashMap<>();
        for (Object[] result : results) {
            String specialization = (String) result[0];
            Long count = (Long) result[1];
            specialityWiseCount.put(specialization, count);
        }
        return specialityWiseCount;
    }

    public Map<String, Long> getCurrentlyAvailableSpecialityWiseDoctorsCount() {
        LocalTime now = LocalTime.now();
        List<Object[]> results = doctorRepository.countAvailableDoctorsBySpecialization(now);

        Map<String, Long> specialityWiseAvailableDoctors = new HashMap<>();
        for (Object[] result : results) {
            String specialization = (String) result[0];
            Long count = (Long) result[1];
            specialityWiseAvailableDoctors.put(specialization, count);
        }
        return specialityWiseAvailableDoctors;
    }

    @Override
    public ResponseEntity<List<Ward>> getAllWards(String jwt) {
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if (role.equals("doctor")) {
            List<Ward> wards = wardRepository.findAll();
            return ResponseEntity.ok().body(wards);
        } else {
            return ResponseEntity.badRequest().body(null);
        }

    }

}
