package org.had.hospitalinformationsystem.ward;

import org.springframework.http.ResponseEntity;

import java.util.*;


public interface WardService {

    List<Ward> bookedWard(String jwt);

    public void createInitialWards();

    public ResponseEntity<Map<String, String>> callEmergency(String jwt, Long wardId);

}
