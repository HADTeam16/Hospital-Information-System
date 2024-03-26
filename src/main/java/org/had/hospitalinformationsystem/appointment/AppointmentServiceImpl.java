package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.doctor.DoctorService;

import org.had.hospitalinformationsystem.dto.AppointmentDto;

import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    DoctorService doctorService;
    @Override
    public Appointment createAppointment(AppointmentDto appointmentDto) {

        Appointment appointment=new Appointment();
        Doctor doctor=doctorRepository.findById(appointmentDto.getDoctorId()).orElseThrow();

        LocalDateTime nextAvailableSlot=doctorService.findNextAvailableSlot(appointmentDto.getDoctorId());

        if(nextAvailableSlot==null){
            throw new IllegalStateException("No available slots for today");
        }
        Patient patient=patientRepository.findById(appointmentDto.getPatientId()).orElseThrow();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setPurpose(appointmentDto.getPurpose());
        appointment.setSlot(nextAvailableSlot);
        appointment.setTemperature(appointment.getTemperature());
        appointment.setBloodPressure(appointment.getBloodPressure());
        appointment.setWeight(appointment.getWeight());
        appointment.setHeight(appointment.getHeight());
        return appointmentRepository.save(appointment);
    }


}
