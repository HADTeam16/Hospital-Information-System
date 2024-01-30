package org.had.hospitalinformationsystem.repository;

import org.had.hospitalinformationsystem.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface AppointmentRepository  extends JpaRepository<Appointment, Long> {

//    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.date = :appointmentDate")
//    List<Appointment> findAppointmentsByDoctorAndDate(@Param("doctorId") String doctorId, @Param("appointmentDate") Date appointmentDate);

    @Query("SELECT a FROM Appointment a " + "WHERE a.doctor.id = :doctorId " +  "AND a.slot >= :appointmentDate " + "AND a.slot < :nextDayAppointmentDate")
    List<Appointment> findAppointmentsByDoctorAndDate( @Param("doctorId") Long doctorId, @Param("appointmentDate") LocalDateTime appointmentDate, @Param("nextDayAppointmentDate") LocalDateTime nextDayAppointmentDate
    );

}
