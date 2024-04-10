package org.had.hospitalinformationsystem.doctor;

import org.had.hospitalinformationsystem.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {

    public Doctor findByUser(User user);

    @Query("SELECT d FROM Doctor d WHERE d.specialization = :specialization")
    List<Doctor> findDoctorBySpecialization(@Param("specialization") String specialization);

    @Query("SELECT DISTINCT d.specialization from Doctor d")
    List<String> findDistinctSpecialization();

    @Query("SELECT d.specialization, COUNT(d) FROM Doctor d GROUP BY d.specialization")
    List<Object[]> countDoctorsBySpecialization();

    @Query("SELECT d.specialization, COUNT(d) FROM Doctor d WHERE :currentTime BETWEEN d.workStart AND d.workEnd GROUP BY d.specialization")
    List<Object[]> countAvailableDoctorsBySpecialization(@Param("currentTime") LocalTime currentTime);

}
