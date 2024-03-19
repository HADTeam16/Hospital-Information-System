package org.had.hospitalinformationsystem.needWard;


import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/need/ward")
public class WardController {

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    NeedWardRepository needWardRepository;

    @PostMapping("/assign/ward/{appointmentId}")
    public void assignWard(@RequestHeader("Authorization") String jwt, @PathVariable Long appointmentId){
        NeedWard needWard = new NeedWard();
        needWard.setAppointment(appointmentRepository.findByAppointmentId(appointmentId));
        needWardRepository.save(needWard);
    }

    @DeleteMapping("/no/need/of/ward/{appointmentId}")
    public void noNeedOfWard(@RequestHeader("Authorization") String jwt, @PathVariable Long appointmentId){
        if(needWardRepository.findByAppointment_AppointmentId(appointmentId)){
            needWardRepository.deleteByAppointment_AppointmentId(appointmentId);
        }
    }

}
