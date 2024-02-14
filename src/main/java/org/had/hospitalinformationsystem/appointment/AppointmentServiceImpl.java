package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    AppointmentRepository appointmentRepository;
    @Override
    public Appointment createAppointment(AppointmentDto appointmentDto) {
        Appointment appointment=new Appointment();
        Doctor doctor=doctorRepository.findById(appointmentDto.getDoctorId()).orElseThrow();
        Patient patient=patientRepository.findById(appointmentDto.getPatientId()).orElseThrow();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setPurpose(appointmentDto.getPurpose());
        appointment.setSlot(appointmentDto.getSlot());
        return appointmentRepository.save(appointment);
    }


}
