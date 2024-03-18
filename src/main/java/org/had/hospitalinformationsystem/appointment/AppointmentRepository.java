package org.had.hospitalinformationsystem.appointment;

import com.twilio.rest.microvisor.v1.App;
import org.had.hospitalinformationsystem.appointment.Appointment;
import org.had.hospitalinformationsystem.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    @Query("SELECT a.patient FROM Appointment a WHERE a.doctor.doctorId = :doctorId")
    List<Patient>getDoctorsAppointment(Long doctorId);
    @Query ("SELECT a FROM Appointment a WHERE a.doctor.doctorId=:doctorId AND a.slot>=:start AND a.slot<:end")
    List<Appointment> findByDoctorIdAndSlotBetween(Long doctorId, LocalDateTime start,LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.doctorId = :doctorId " + "AND a.slot >= :startDate AND a.slot < :endDate")
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDateTime startDate, LocalDateTime endDate);

    Appointment findByAppointmentId(Long appointmentId);
    @Query("SELECT a FROM Appointment a where a.patient.id= :patientId AND a.slot< :startDate")
    List<Appointment> findAllAppointmentforPatient(Long patientId,LocalDateTime startDate);

}
