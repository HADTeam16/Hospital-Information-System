package org.had.hospitalinformationsystem.controller;

import org.had.hospitalinformationsystem.config.JwtProvider;
import org.had.hospitalinformationsystem.model.Appointment;
import org.had.hospitalinformationsystem.model.Doctor;
import org.had.hospitalinformationsystem.model.Patient;
import org.had.hospitalinformationsystem.model.User;
import org.had.hospitalinformationsystem.repository.AppointmentRepository;
import org.had.hospitalinformationsystem.repository.DoctorRepository;
import org.had.hospitalinformationsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AppointmentRepository  appointmentRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @GetMapping()
    public List<Appointment>getAllAppointment(@RequestHeader("Authorization") String jwt){
        return appointmentRepository.findAll();
    }


    @GetMapping("/patientDetails")
    public  List<Patient>getDoctorsAppointment(@RequestHeader("Authorization") String jwt){
        String userName= JwtProvider.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUserName(userName);
        Doctor doctor = doctorRepository.findByuser(user);
        return appointmentRepository.getDoctorsAppointment(doctor.getDoctorId());
    }

}
