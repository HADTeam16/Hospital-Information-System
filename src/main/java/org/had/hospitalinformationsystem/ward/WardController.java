package org.had.hospitalinformationsystem.ward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ward/details")
public class WardController {

    @Autowired
    WardService wardService;

    @GetMapping("/booked/ward")
    public List<Ward> bookedWard(@RequestHeader("Authorization") String jwt){
        return wardService.bookedWard(jwt);
    }
}
