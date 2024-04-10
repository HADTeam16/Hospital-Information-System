package org.had.hospitalinformationsystem.ward;

import org.had.hospitalinformationsystem.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WardRepository extends JpaRepository<Ward,Long> {

    @Query("SELECT wd FROM Ward wd WHERE wd.availableStatus = true")
    List<Ward>findAvailableWard();

    @Query("SELECT wd FROM Ward wd WHERE wd.availableStatus = false")
    List<Ward>findBookedWard();

    @Query("SELECT wd.patient FROM Ward wd WHERE wd.managingNurse.nurseId=:nurseId")
    List<Patient> assignedPatientsUnderNurse(Long nurseId);

    @Query("SELECT wd FROM Ward wd WHERE wd.managingNurse.nurseId=:nurseId")
    List<Ward> allottedWard(Long nurseId);

    @Query("SELECT wd.wardId FROM Ward wd WHERE wd.availableStatus = true")
    List<Long>findAvailableWardIds();




}
