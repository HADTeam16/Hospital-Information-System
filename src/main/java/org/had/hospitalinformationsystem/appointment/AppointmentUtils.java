package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.doctor.DoctorService;
import org.had.hospitalinformationsystem.dto.AppointmentDto;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.utility.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AppointmentUtils extends Utils {

    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    DoctorService doctorService;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    protected Appointment createAppointment(AppointmentDto appointmentDto) {
        Doctor doctor = findDoctorById(appointmentDto.getDoctorId());
        LocalDateTime nextAvailableSlot = findNextAvailableSlot(doctor);
        if (nextAvailableSlot == null) {
            throw new IllegalStateException("No available slots for today");
        }
        Patient patient = findPatientById(appointmentDto.getPatientId());
        Appointment appointment = buildAppointment(doctor, patient, appointmentDto, nextAvailableSlot);
        return appointmentRepository.save(appointment);
    }

    protected Appointment buildAppointment(Doctor doctor, Patient patient, AppointmentDto appointmentDto, LocalDateTime nextAvailableSlot) {
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setPurpose(appointmentDto.getPurpose());
        appointment.setSlot(nextAvailableSlot);
        appointment.setNeedWard(false); // Assuming default value
        appointment.setCompleted(0); // Assuming default value
        appointment.setTemperature(appointmentDto.getTemperature());
        appointment.setBloodPressure(appointmentDto.getBloodPressure());
        appointment.setWeight(appointmentDto.getWeight());
        appointment.setHeight(appointmentDto.getHeight());
        return appointment;
    }

    protected Doctor findDoctorById(long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));
    }

    protected LocalDateTime findNextAvailableSlot(Doctor doctor) {
        Long doctorId = doctor.getDoctorId();
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        for (int dayOffset = 0; dayOffset < 2; dayOffset++) {

            LocalDate checkDate = today.plusDays(dayOffset);
            LocalDateTime workStartToday = LocalDateTime.of(checkDate, doctor.getWorkStart());
            LocalDateTime workEndToday = LocalDateTime.of(checkDate, doctor.getWorkEnd());

            if (dayOffset == 0 && now.isAfter(workEndToday)) {
                continue;
            }

            LocalDateTime startSlot = (dayOffset == 0 && now.isAfter(workStartToday)) ? now : workStartToday;
            if (startSlot.getMinute() >= 30) {
                startSlot = startSlot.plusHours(1).withMinute(0).withSecond(0).withNano(0);
            } else if (startSlot.getMinute() > 0 && startSlot.getMinute() < 30) {
                startSlot = startSlot.withMinute(30).withSecond(0).withNano(0);
            }

            while (startSlot.isBefore(workEndToday)) {
                if (isDoctorAvailable(doctorId, startSlot)) {

                    return startSlot; // Found an available slot
                }
                startSlot = startSlot.plusMinutes(30);
            }

        }
        return null;
    }

    protected Patient findPatientById(long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + patientId));
    }

    protected void notifyDoctor(Appointment appointment) {
        Doctor doctor = appointment.getDoctor();
        messagingTemplate.convertAndSendToUser(doctor.getUser().getUserName(),"/topic/appointments", appointment);
    }

    private boolean isDoctorAvailable(Long doctorId, LocalDateTime desiredSlot) {
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

}
