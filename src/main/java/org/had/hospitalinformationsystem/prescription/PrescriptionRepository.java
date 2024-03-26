package org.had.hospitalinformationsystem.prescription;


import org.had.hospitalinformationsystem.prescription.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription,Long> {

//    @Query("SELECT p.prescription FROM Prescription p WHERE p.appointment.appointmentId = :appointmentId")
//    public List<String> findPrescription(Long appointmentId);
//
    //yeh one to many ke liye tha ab bss one to one hai

    @Query("SELECT p From Prescription p where p.appointment.appointmentId= :appointmentId")
    public Prescription findPrescriptionByAppointmentID(Long appointmentId);

    @Query("SELECT p.prescription From Prescription p where p.appointment.appointmentId= :appointmentId")
    public List<String> findPrescriptionImageByAppointmentID(Long appointmentId);

}
