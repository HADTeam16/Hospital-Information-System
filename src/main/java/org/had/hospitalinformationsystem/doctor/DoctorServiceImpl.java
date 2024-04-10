package org.had.hospitalinformationsystem.doctor;

import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.dto.PrescriptionsAndRecords;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.needWard.NeedWard;
import org.had.hospitalinformationsystem.needWard.NeedWardRepository;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.prescription.Prescription;
import org.had.hospitalinformationsystem.prescription.PrescriptionRepository;
import org.had.hospitalinformationsystem.records.Records;
import org.had.hospitalinformationsystem.records.RecordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImpl implements DoctorService{
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

    @Override
    public boolean isDoctorAvailable(Long doctorId, LocalDateTime desiredSlot) {
        Optional<Doctor> doctorOpt=doctorRepository.findById(doctorId);
        if(!doctorOpt.isPresent()){
            return false;
        }

        Doctor doctor=doctorOpt.get();
        if(desiredSlot.toLocalTime().isBefore(doctor.getWorkStart()) ||
                desiredSlot.toLocalTime().isAfter(doctor.getWorkEnd().minusMinutes(30))){
            return false;
        }
        List<Appointment> appointments=appointmentRepository.findByDoctorIdAndSlotBetween(doctorId,desiredSlot,desiredSlot.plusMinutes(30));
        return appointments.isEmpty();
    }

    @Override
    public LocalDateTime findNextAvailableSlot(Long doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // Attempt to find an available slot over today and the next two days
        for (int dayOffset = 0; dayOffset < 2; dayOffset++) {

            LocalDate checkDate = today.plusDays(dayOffset);
            LocalDateTime workStartToday = LocalDateTime.of(checkDate, doctor.getWorkStart());
            LocalDateTime workEndToday = LocalDateTime.of(checkDate, doctor.getWorkEnd());

            // If checking today, but we're already past work hours, skip to the next day
            if (dayOffset == 0 && now.isAfter(workEndToday)) {
                continue;
            }

            // Determine the starting slot for checking availability
            LocalDateTime startSlot = (dayOffset == 0 && now.isAfter(workStartToday)) ? now : workStartToday;
            if (startSlot.getMinute() >= 30) {
                startSlot = startSlot.plusHours(1).withMinute(0).withSecond(0).withNano(0);
            } else if (startSlot.getMinute() > 0 && startSlot.getMinute() < 30) {
                startSlot = startSlot.withMinute(30).withSecond(0).withNano(0);
            }

            // Find the next available slot within the doctor's working hours for the day
            while (startSlot.isBefore(workEndToday)) {
                if (isDoctorAvailable(doctorId, startSlot)) {

                    return startSlot; // Found an available slot
                }
                startSlot = startSlot.plusMinutes(30);
            }

        }
        return null;
    }

    @Override
    public List<Doctor> getDoctorsWhoAreSurgeon(List<Doctor> doctors) {
        List<Doctor> surgeons=new ArrayList<>();
        for(Doctor doctor:doctors){
            if(doctor.getSpecialization().contains("surgeon")){
                surgeons.add(doctor);
            }
        }
        return surgeons;
    }
    @Override
    public ResponseEntity<?> finishAppointment(String jwt,
                                               PrescriptionsAndRecords prescriptionsAndRecords,
                                               Long wardFlag) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (!role.equals("doctor")) {
                return ResponseEntity.badRequest().body("Only doctor can finish appointment");
            }
            Appointment appointment = appointmentRepository.findByAppointmentId(prescriptionsAndRecords.getAppointment().getAppointmentId());
            if (appointment == null) {
                return ResponseEntity.badRequest().body("No appointment found");
            }

            if (wardFlag.equals(1L)) {
                NeedWard needWard = new NeedWard();
                needWard.setAppointment(appointment);
                needWard.setRequestTime(LocalDateTime.now());
                appointment.setNeedWard(true);
                needWardRepository.save(needWard);
            }

            Prescription prescription = new Prescription();
            prescription.setPrescription(prescriptionsAndRecords.getPrescription());
            prescription.setAppointment(appointment);
            prescriptionRepository.save(prescription);

            for (String recordImage : prescriptionsAndRecords.getRecords()) {
                Records newRecord = new Records();
                newRecord.setRecordImage(recordImage);
                newRecord.setAppointment(appointment);
                recordsRepository.save(newRecord);
            }
            appointment.setCompleted(1);
            appointmentRepository.save(appointment);

            return ResponseEntity.ok("Appointment finished successfully");
        } catch (Exception e) {

            System.err.println("An error occurred in finishAppointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while finishing the appointment.\n "+e.getMessage());
        }
    }

    @Override
    public Map<String, Long> getSpecialityWiseDoctorsCount() {
        List<Object[]> results=doctorRepository.countDoctorsBySpecialization();

        Map<String,Long> specialityWiseCount=new HashMap<>();
        for(Object[] result:results){
            String specialization=(String) result[0];
            Long count=(Long) result[1];
            specialityWiseCount.put(specialization,count);
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


}
