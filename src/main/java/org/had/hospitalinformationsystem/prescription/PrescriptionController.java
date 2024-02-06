package org.had.hospitalinformationsystem.prescription;

import org.had.hospitalinformationsystem.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PrescriptionRepository prescriptionRepository;


    @GetMapping("/{appointmentId}")
    public List<String> getPrescription(@PathVariable Long appointmentId){
        return prescriptionRepository.findPrescription(appointmentId);
    }
}
