package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    Appointment findByAppointmentId(Long appointmentId);

    @Query("SELECT a.patient FROM Appointment a WHERE a.doctor.doctorId = :doctorId")
    List<Patient>getDoctorsAppointment(Long doctorId);

    @Query ("SELECT a FROM Appointment a WHERE a.doctor.doctorId=:doctorId AND a.slot>=:start AND a.slot<:end")
    List<Appointment> findByDoctorIdAndSlotBetween(Long doctorId, LocalDateTime start,LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.doctorId = :doctorId " + "AND a.slot >= :startDate AND a.slot < :endDate ORDER BY a.slot")
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDateTime startDate, LocalDateTime endDate);
    @Query("SELECT a.appointmentId, a.slot FROM Appointment a where a.patient.consent=true")
    List<Appointment> findAllAppointment();
    @Query("SELECT a.appointmentId, a.slot FROM Appointment a where a.patient.id= :patientId AND a.patient.consent=true AND a.slot< :startDate ORDER BY a.slot DESC")
    List<Object[]> findAllPreviousAppointmentForPatient(Long patientId, LocalDateTime startDate);


    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.slot > :now")
    Long getScheduledAppointmentCount(@Param("now") LocalDateTime now);

    @Query("SELECT a from Appointment a where a.completed= :completed AND a.doctor.doctorId= :doctorId")
    List<Appointment> getAttendedAppointments(@Param("completed") Integer completed,Long doctorId);

    @Query("SELECT COUNT(DISTINCT a.patient.id) FROM Appointment a WHERE a.completed = :completed AND a.doctor.doctorId = :doctorId")
    Integer getDistinctAttendedPatientsCount(@Param("completed") Integer completed,@Param("doctorId") Long doctorId);

    @Query("SELECT a FROM Appointment a WHERE a.needWard=true AND a.doctor.doctorId = :doctorId")
    List<Appointment> getWardAssignedTillDate(@Param("doctorId") Long doctorId);
}
