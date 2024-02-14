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
    @Override
    public List<Doctor> getAvailableDoctorsBySpecializationAndSlot(String specialization, LocalDateTime slot) {
        List<Doctor>specializedDoctors=doctorRepository.findDoctorBySpecialization(specialization);
        return specializedDoctors.stream().filter(
                doctor->isDoctorAvailable(doctor.getDoctorId(), slot)
        ).collect(Collectors.toList());
    }

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
        List<Appointment> appointments=appointmentRepository.findByDoctorIdAndSlotBetween(doctorId,desiredSlot,desiredSlot.plusHours(1));
        return appointments.isEmpty();
    }
}
