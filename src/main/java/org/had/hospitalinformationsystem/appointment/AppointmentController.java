package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.auth.JwtProvider;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.user.UserRepository;
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
