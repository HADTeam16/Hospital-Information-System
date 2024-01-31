package org.had.hospitalinformationsystem.controller;

import org.had.hospitalinformationsystem.model.Appointment;
import org.had.hospitalinformationsystem.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    AppointmentRepository  appointmentRepository;

    @GetMapping()
    public List<Appointment>getAllAppointment(@RequestHeader("Authorization") String jwt){
        return appointmentRepository.findAll();
    }

    @GetMapping("/patientDetails/{doctorId}")
    public  List<Appointment>getDoctorsAppointment(@RequestHeader("Authorization") String jwt, @PathVariable Long doctorId){
        return appointmentRepository.getDoctorsAppointment(doctorId);
    }

}
