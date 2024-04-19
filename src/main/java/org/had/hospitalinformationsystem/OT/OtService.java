package org.had.hospitalinformationsystem.OT;

import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Set;


public interface OtService {


    ResponseEntity<?> getAllOts(String jwt);

    ResponseEntity<String> createOts(String jwt);

    ResponseEntity<?> getAllSurgeons(String jwt);

    ResponseEntity<Map<String, String>> bookOt(String jwt, Long otId, Set<Long> surgeonIds);

    ResponseEntity<?> freeOt(String jwt, Long otId);
}
