package org.had.hospitalinformationsystem.repository;

import org.had.hospitalinformationsystem.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    @Query("SELECT a FROM Appointment a WHERE a.doctor.doctorId = :doctorId")
    List<Appointment>getDoctorsAppointment(Long doctorId);

}
