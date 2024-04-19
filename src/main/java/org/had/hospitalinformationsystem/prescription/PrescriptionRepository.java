package org.had.hospitalinformationsystem.prescription;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription,Long> {

    @Query("SELECT p From Prescription p where p.appointment.appointmentId= :appointmentId")
    public Prescription findPrescriptionByAppointmentID(Long appointmentId);

    @Query("SELECT p.prescription From Prescription p where p.appointment.appointmentId= :appointmentId")
    public String findPrescriptionImageByAppointmentID(Long appointmentId);

}
