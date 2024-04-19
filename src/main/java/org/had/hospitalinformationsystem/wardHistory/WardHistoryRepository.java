package org.had.hospitalinformationsystem.wardHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WardHistoryRepository extends JpaRepository<WardHistory,Long> {
    @Query("SELECT wh from WardHistory wh WHERE wh.appointment.appointmentId=:AppointmentId")
    List<WardHistory> getWardHistoriesByAppointment(Long AppointmentId);
}
