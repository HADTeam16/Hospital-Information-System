package org.had.hospitalinformationsystem.ward;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WardRepository extends JpaRepository<WardDetails,Long> {

    @Query("SELECT wd FROM WardDetails wd WHERE wd.availableStatus = true")
    List<WardDetails>findAvailableWard();

    @Query("SELECT wd FROM WardDetails wd WHERE wd.availableStatus = false")
    List<WardDetails>findBookedWard();
}
