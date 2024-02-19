package org.had.hospitalinformationsystem.doctor;


import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    AppointmentRepository appointmentRepository;


    @GetMapping("/getalldoctors")
    public List<Doctor>getAllDoctor(@RequestHeader("Authorization") String jwt){
        List<Doctor>allDoctor=null;
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if(role.equals("admin") || role.equals("receptionist")){
            allDoctor = doctorRepository.findAll();
        }
        return allDoctor;
    }

}
