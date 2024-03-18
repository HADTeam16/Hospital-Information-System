package org.had.hospitalinformationsystem.consent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsentRepository extends JpaRepository<Consent,Long> {

    @Query("SELECT c FROM Consent c WHERE c.patient.id = :patientId")
    Consent getConcentByPatientId(Long patientId);
}
