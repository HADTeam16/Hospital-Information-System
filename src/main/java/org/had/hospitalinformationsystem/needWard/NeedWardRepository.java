package org.had.hospitalinformationsystem.needWard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NeedWardRepository extends JpaRepository<NeedWard,Long> {

    @Query("Select n FROM NeedWard n")
    List<NeedWard> returnNeedWards();
}
