package org.had.hospitalinformationsystem.repository;

import org.had.hospitalinformationsystem.model.Doctor;
import org.had.hospitalinformationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    public Doctor findByUserName(String username);

    public Doctor findByEmail(String email);


    @Query("SELECT d FROM Doctor d WHERE d.role = ?1")
    public List<Doctor> findAllByRole(String role);

    @Query("SELECT d FROM Doctor d WHERE d.specialization = ?1")
    public List<Doctor> findUserBySpecialization(String specialization);


    @Query("select d from Doctor d where d.firstName LIKE %:query% OR d.lastName LIKE %:query% OR d.userName LIKE %:query%")
    public List<Doctor> searchDoctor(@Param("query") String query);
}
