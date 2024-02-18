package org.had.hospitalinformationsystem.doctor;

import java.time.LocalDateTime;
import java.util.List;

public interface DoctorService {
//    List<Doctor> getAvailableDoctorsBySpecializationAndSlot(String specialization, LocalDateTime slot);
    boolean isDoctorAvailable(Long doctorId, LocalDateTime desiredSlot);

    LocalDateTime findNextAvailableSlot(Long doctorId);

}
