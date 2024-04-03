package org.had.hospitalinformationsystem.OT;

import org.apache.coyote.Response;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.doctor.DoctorService;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ot")
public class OtController {
    @Autowired
    OTRepository otRepository;
    @Autowired
    OtService otService;
    @Autowired
    DoctorService doctorService;
    @Autowired
    DoctorRepository doctorRepository;


    @GetMapping("/get/all/ots")
    ResponseEntity<List<OT>> getAllOts(@RequestHeader("Authorization") String jwt){
        String role= JwtProvider.getRoleFromJwtToken(jwt);
        if(role.equals("receptionist")){
            List<OT> ots=otRepository.findAll();
            return ResponseEntity.ok().body(ots);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping("/create/ots")
    ResponseEntity<String> createOts(@RequestHeader("Authorization") String jwt){
        String role= JwtProvider.getRoleFromJwtToken(jwt);
        if(role.equals("receptionist")){
            otService.createOts();
            return ResponseEntity.ok().body("Ots succesfully created");
        }
        else{
            return ResponseEntity.badRequest().body("Only receptionist can create ots");
        }
    }
    @GetMapping("/get/all/free/surgeons")
    ResponseEntity<List<Doctor>> getAllSurgeons(@RequestHeader("Authorization") String jwt){
        String role= JwtProvider.getRoleFromJwtToken(jwt);
        if(role.equals("receptionist")){
            List<Doctor> surgeons=doctorService.getDoctorsWhoAreSurgeon(doctorRepository.findAll());
            List<OT> activeOts=otRepository.findActiveOt();
            Set<Long> busyDoctorIds = activeOts.stream()
                    .flatMap(ot -> ot.getDoctors().stream())
                    .map(Doctor::getDoctorId)
                    .collect(Collectors.toSet());
            List<Doctor> freeSurgeons = surgeons.stream()
                    .filter(surgeon -> !busyDoctorIds.contains(surgeon.getDoctorId()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(freeSurgeons);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PutMapping("/book/ot/{otId}")
    ResponseEntity<?> bookOt(@RequestHeader("Authorization") String jwt,@PathVariable Long otId,@RequestBody Set<Long> surgeonIds){
        String role= JwtProvider.getRoleFromJwtToken(jwt);
        if(role.equals("receptionist")){
            Optional<OT> otOptional=otRepository.findById(otId);
            if(!otOptional.isPresent()){
                return ResponseEntity.notFound().build();
            }
            OT ot=otOptional.get();
            if(!ot.isAvailableStatus()){
                return ResponseEntity.badRequest().body("OT is not available for booking.");
            }

            Set<Doctor> doctors=surgeonIds.stream().map(id->doctorRepository.findById(id))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            if(doctors.size()!=surgeonIds.size()){
                return ResponseEntity.badRequest().body("One or more surgeon IDs are invalid");
            }
            ot.setDoctors(doctors);
            ot.setAvailableStatus(false);
            otRepository.save(ot);
            return ResponseEntity.ok(ot);
        }
        else{
            return ResponseEntity.badRequest().body("Only receptionists can book OTs.");
        }
    }
    //free ot
    @PutMapping("/free/ot/{otId}")
    ResponseEntity<?> freeOt(@RequestHeader("Authorization") String jwt,@PathVariable Long otId){
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        // Check if the user has the correct role to free an OT
        if (!role.equals("receptionist") && !role.equals("admin")) {
            return ResponseEntity.ok("Unauthorized: Only receptionists or admins can free an OT.");
        }

        // Fetch the OT by ID
        Optional<OT> otOptional = otRepository.findById(otId);
        if (!otOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        OT ot = otOptional.get();

        // Update the OT status to available and clear any associated surgeons or patients
        ot.setAvailableStatus(true);
        // Assuming there's a method to clear assigned doctors and patients
        ot.setDoctors(Collections.emptySet());

        // Save the updated OT
        otRepository.save(ot);
        return ResponseEntity.ok("OT freed and is now available.");
    }
}
