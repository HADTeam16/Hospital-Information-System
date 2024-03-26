package org.had.hospitalinformationsystem.records;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordsRepository extends JpaRepository<Records,Long> {

    @Query("SELECT r FROM Records r WHERE r.appointment.appointmentId = ?1")
    List<Records> findRecordsByAppointmentId(Long appointmentId);

    @Query("SELECT r from Records r WHERE r.appointment.patient.id = ?1")
    List<Records> findRecordsByPatientId(Long patientId);
}
