package org.had.hospitalinformationsystem.repository;

import org.had.hospitalinformationsystem.model.Doctor;
import org.had.hospitalinformationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    public Doctor findByDoctorName(String doctorName);

    public Doctor findByEmail(String email);



    @Query("select u from Doctor u where u.firstName LIKE %:query% OR u.lastName LIKE %:query% OR u.doctorName LIKE %:query%")
    public List<Doctor> searchDoctor(@Param("query") String query);
}
