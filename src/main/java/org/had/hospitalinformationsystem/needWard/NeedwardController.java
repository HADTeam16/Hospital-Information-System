package org.had.hospitalinformationsystem.needWard;


import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/need/ward")
public class NeedwardController {

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    NeedWardRepository needWardRepository;

}
