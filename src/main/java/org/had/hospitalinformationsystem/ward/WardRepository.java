package org.had.hospitalinformationsystem.ward;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WardRepository extends JpaRepository<Ward,Long> {

    @Query("SELECT wd FROM Ward wd WHERE wd.availableStatus = true")
    List<Ward>findAvailableWard();

    @Query("SELECT wd FROM Ward wd WHERE wd.availableStatus = false")
    List<Ward>findBookedWard();
}
