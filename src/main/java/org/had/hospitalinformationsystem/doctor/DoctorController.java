package org.had.hospitalinformationsystem.doctor;


import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
