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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    ResponseEntity<?> getAllOts(@RequestHeader("Authorization") String jwt) {
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if (role.equals("receptionist")) {
            List<OT> ots = otRepository.findAll();
            return ResponseEntity.ok().body(ots);
        } else {
            return ResponseEntity.badRequest().body("Only receptionist can see all OTs");
        }
    }

    @GetMapping("/create/ots")
    ResponseEntity<String> createOts(@RequestHeader("Authorization") String jwt) {
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if (role.equals("receptionist")) {
            otService.createOts();
            return ResponseEntity.ok().body("Ots succesfully created");
        } else {
            return ResponseEntity.badRequest().body("Only receptionist can create ots");
        }
    }

    @GetMapping("/get/all/free/surgeons")
    ResponseEntity<?> getAllSurgeons(@RequestHeader("Authorization") String jwt) {
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if (role.equals("receptionist")) {
            List<Doctor> surgeons = doctorService.getDoctorsWhoAreSurgeon(doctorRepository.findAll());
            List<OT> activeOts = otRepository.findActiveOt();
            Set<Long> busyDoctorIds = activeOts.stream()
                    .flatMap(ot -> ot.getDoctors().stream())
                    .map(Doctor::getDoctorId)
                    .collect(Collectors.toSet());
            List<Doctor> freeSurgeons = surgeons.stream()
                    .filter(surgeon -> !busyDoctorIds.contains(surgeon.getDoctorId()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(freeSurgeons);
        } else {
            return ResponseEntity.badRequest().body("Only receptionist can view all surgeons");
        }
    }

    @PutMapping("/book/ot/{otId}")
    ResponseEntity<Map<String, String>> bookOt(@RequestHeader("Authorization") String jwt, @PathVariable Long otId,
            @RequestBody Set<Long> surgeonIds) {
        Map<String, String> response = new HashMap<>();
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if (role.equals("receptionist")) {
            Optional<OT> otOptional = otRepository.findById(otId);
            if (!otOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            OT ot = otOptional.get();
            if (!ot.isAvailableStatus()) {
                response.put("message", "OT is not available for booking!");
                return ResponseEntity.badRequest().body(response);
            }

            Set<Doctor> doctors = surgeonIds.stream().map(id -> doctorRepository.findById(id))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(doctor -> doctor.getSpecialization().contains("surgeon"))
                    .collect(Collectors.toSet());
            if (doctors.size() != surgeonIds.size()) {
                response.put("message", "One or more surgeon IDs are invalid!");
                return ResponseEntity.badRequest().body(response);
            }
            ot.setDoctors(doctors);
            ot.setAvailableStatus(false);
            otRepository.save(ot);
            response.put("message", "OT booking successful");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Only receptionists can book OTs!");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // free ot
    @GetMapping("/free/ot/{otId}")
    ResponseEntity<?> freeOt(@RequestHeader("Authorization") String jwt, @PathVariable Long otId) {
        Map<String, String> response = new HashMap<>();
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        // Check if the user has the correct role to free an OT
        if (!role.equals("receptionist") && !role.equals("admin")) {
            response.put("message", "Unauthorized: Only receptionists or admins can free an OT!");
            return ResponseEntity.ok(response);
        }

        // Fetch the OT by ID
        Optional<OT> otOptional = otRepository.findById(otId);
        if (!otOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        OT ot = otOptional.get();

        // Update the OT status to available and clear any associated surgeons or
        // patients
        if (ot.isAvailableStatus()) {
            response.put("message", "OT is already available");
            return ResponseEntity.badRequest().body(response);
        }
        ot.setAvailableStatus(true);
        // Assuming there's a method to clear assigned doctors and patients
        ot.setDoctors(Collections.emptySet());

        // Save the updated OT
        otRepository.save(ot);
        response.put("message", "OT cleared successfully");
        return ResponseEntity.ok(response);
    }
}
