package org.had.hospitalinformationsystem.OT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/ot")
public class OtController {
    @Autowired
    OtService otService;

    @GetMapping("/get/all/ots")
    ResponseEntity<?> getAllOts(@RequestHeader("Authorization") String jwt) {
        return otService.getAllOts(jwt);
    }

    @GetMapping("/create/ots")
    ResponseEntity<String> createOts(@RequestHeader("Authorization") String jwt) {
        return otService.createOts(jwt);
    }

    @GetMapping("/get/all/free/surgeons")
    ResponseEntity<?> getAllSurgeons(@RequestHeader("Authorization") String jwt) {
        return otService.getAllSurgeons(jwt);
    }

    @PutMapping("/book/ot/{otId}")
    ResponseEntity<Map<String, String>> bookOt(@RequestHeader("Authorization") String jwt, @PathVariable Long otId, @RequestBody Set<Long> surgeonIds) {
        return otService.bookOt(jwt, otId, surgeonIds);
    }

    @GetMapping("/free/ot/{otId}")
    ResponseEntity<?> freeOt(@RequestHeader("Authorization") String jwt, @PathVariable Long otId) {
        return otService.freeOt(jwt, otId);
    }
}