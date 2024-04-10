package org.had.hospitalinformationsystem.ward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ward/details")
public class WardController {

    @Autowired
    WardRepository wardRepository;

//    @GetMapping("/available/ward")
//    public List<Ward> availableWard(@RequestHeader("Authorization") String jwt){
//        return wardRepository.findAvailableWard();
//    }

    @GetMapping("/booked/ward")
    public List<Ward> bookedWard(@RequestHeader("Authorization") String jwt){
        return wardRepository.findBookedWard();
    }
}
