package org.had.hospitalinformationsystem.concern;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcernRepository extends JpaRepository<Concern,Long> {

    @Query("SELECT c FROM Concern c WHERE c.patient.id = :patientId")
    Concern getConcernByPatientId(Long patientId);
}
