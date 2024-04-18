package org.had.hospitalinformationsystem.wardHistory;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WardHistoryService {
    ResponseEntity<?> getWardHistory(String jwt,Long wardId);
}
