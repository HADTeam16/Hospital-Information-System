package org.had.hospitalinformationsystem.patient;

import org.had.hospitalinformationsystem.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient,Long> {

    @Query("SELECT p FROM Patient p WHERE p.user.userName = :str OR p.user.contact = :str")
    public Patient findPatientByUserName(@Param("str") String str);

    @Query("SELECT p FROM Patient p WHERE p.user.contact = :contact")
    public List<Patient> findPatientByContact(@Param("contact") String contact);

//    @Query("SELECT p FROM Patient p WHERE p.needWard=true")
//    public List<Patient> findPatientWhoNeedsWard();
}
