package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    @Query("SELECT a.patient FROM Appointment a WHERE a.doctor.doctorId = :doctorId")
    List<Patient>getDoctorsAppointment(Long doctorId);
    @Query ("SELECT a FROM Appointment a WHERE a.doctor.doctorId=:doctorId AND a.slot>=:start AND a.slot<:end")
    List<Appointment> findByDoctorIdAndSlotBetween(Long doctorId, LocalDateTime start,LocalDateTime end);


}
