package org.had.hospitalinformationsystem.OT;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OTRepository extends JpaRepository<OT,Long> {

    @Query("SELECT ot FROM OT ot WHERE ot.availableStatus = true")
    List<OT> findAvailableOt();

    @Query("SELECT ot FROM OT ot WHERE ot.availableStatus = false")
    List<OT> findActiveOt();


}
