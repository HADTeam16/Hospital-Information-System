package org.had.hospitalinformationsystem.doctor;

import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImpl implements DoctorService{
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    AppointmentRepository appointmentRepository;
//    @Override
//    public List<Doctor> getAvailableDoctorsBySpecializationAndSlot(String specialization, LocalDateTime slot) {
//        List<Doctor>specializedDoctors=doctorRepository.findDoctorBySpecialization(specialization);
//        return specializedDoctors.stream().filter(
//                doctor->isDoctorAvailable(doctor.getDoctorId(), slot)
//        ).collect(Collectors.toList());
//    }

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
}
