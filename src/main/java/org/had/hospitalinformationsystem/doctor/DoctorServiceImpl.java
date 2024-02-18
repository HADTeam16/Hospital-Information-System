package org.had.hospitalinformationsystem.doctor;

import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        if(desiredSlot.toLocalTime().isBefore(doctor.getWorkEnd()) ||
                desiredSlot.toLocalTime().isAfter(doctor.getWorkEnd().minusHours(1))){
            return false;
        }
        List<Appointment> appointments=appointmentRepository.findByDoctorIdAndSlotBetween(doctorId,desiredSlot,desiredSlot.plusMinutes(30));
        return appointments.isEmpty();
    }

    @Override
    public LocalDateTime findNextAvailableSlot(Long doctorId) {
        Doctor doctor=doctorRepository.findById(doctorId).orElseThrow();
        LocalDateTime now=LocalDateTime.now();

        LocalDateTime startSlot=now.getMinute()>=30?
                now.withMinute(0).withSecond(0).withNano(0).plusHours(1):
                now.withMinute(30).withSecond(0).withNano(0);
        LocalDateTime endOfWork=now.withHour(doctor.getWorkEnd().getHour()).withHour(0);

        while(startSlot.isBefore(endOfWork)){
            if(isDoctorAvailable(doctorId,startSlot)){
                return startSlot;
            }
            startSlot=startSlot.plusMinutes(30);
        }
        return null;
    }
}
