package org.had.hospitalinformationsystem.OT;

import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.doctor.DoctorService;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OTServiceImplementation implements OtService{

    @Autowired
    OTRepository otRepository;
    @Autowired
    OtService otService;
    @Autowired
    DoctorService doctorService;
    @Autowired
    DoctorRepository doctorRepository;

    private void createOts(){
        for(int i=1;i<=10;i++){
            OT ot=new OT();
            ot.setAvailableStatus(true);
            otRepository.save(ot);
        }
    }

    @Override
    public ResponseEntity<?> getAllOts(String jwt) {
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if (role.equals("receptionist")) {
            List<OT> ots = otRepository.findAll();
            return ResponseEntity.ok().body(ots);
        } else {
            return ResponseEntity.badRequest().body("Only receptionist can see all OTs");
        }
    }

    @Override
    public ResponseEntity<String> createOts(String jwt) {
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if (role.equals("receptionist")) {
            createOts();
            return ResponseEntity.ok().body("Ots succesfully created");
        } else {
            return ResponseEntity.badRequest().body("Only receptionist can create ots");
        }
    }

    @Override
    public ResponseEntity<?> getAllSurgeons(String jwt) {
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

    @Override
    public ResponseEntity<Map<String, String>> bookOt(String jwt,Long otId,Set<Long> surgeonIds) {
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

    @Override
    public ResponseEntity<?> freeOt(String jwt, Long otId) {
        Map<String, String> response = new HashMap<>();
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if (!role.equals("receptionist") && !role.equals("admin")) {
            response.put("message", "Unauthorized: Only receptionists or admins can free an OT!");
            return ResponseEntity.ok(response);
        }
        Optional<OT> otOptional = otRepository.findById(otId);
        if (!otOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        OT ot = otOptional.get();
        if (ot.isAvailableStatus()) {
            response.put("message", "OT is already available");
            return ResponseEntity.badRequest().body(response);
        }
        ot.setAvailableStatus(true);
        ot.setDoctors(Collections.emptySet());
        otRepository.save(ot);
        response.put("message", "OT cleared successfully");
        return ResponseEntity.ok(response);
    }
}
