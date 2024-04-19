package org.had.hospitalinformationsystem.wardHistory;

import org.springframework.http.ResponseEntity;

public interface WardHistoryService {
    ResponseEntity<?> getWardHistory(String jwt,Long wardId);
}
