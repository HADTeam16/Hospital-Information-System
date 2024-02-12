package org.had.hospitalinformationsystem.patient;

import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    PatientRepository patientRepository;

    @GetMapping("/getallpatients")
    public List<Patient>getAllPatient(@RequestHeader("Authorization") String jwt) throws  Exception{
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        List<Patient>allPatient = null;
        if(role.equals("admin") || role.equals("receptionist")){
            allPatient = patientRepository.findAll();
        }
        else{
            throw new Exception("Access Denied!!!");
        }
        return allPatient;
    }
}
