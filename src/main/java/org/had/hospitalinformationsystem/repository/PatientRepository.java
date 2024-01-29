package org.had.hospitalinformationsystem.repository;

import org.had.hospitalinformationsystem.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient,Long> {

    public Patient findByUserName(String userName);

    @Query("select p from Patient p where p.firstName LIKE %:query%  OR p.lastName LIKE %:query% OR p.userName LIKE %:query%")
    public List<Patient> searchPatient(@Param("query") String query);


}
