package org.had.hospitalinformationsystem.controller;


import org.had.hospitalinformationsystem.model.Appointment;
import org.had.hospitalinformationsystem.model.Doctor;
import org.had.hospitalinformationsystem.repository.AppointmentRepository;
import org.had.hospitalinformationsystem.repository.DoctorRepository;
import org.had.hospitalinformationsystem.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

//    @PostMapping("/addAppointment")
//    public String bookAppointment(@PathVariable String userName1,String userName2){
//        Appointment appointment = new Appointment();
//        Doctor doctor = doctorRepository.find
//        appointment.s
//    }

}
