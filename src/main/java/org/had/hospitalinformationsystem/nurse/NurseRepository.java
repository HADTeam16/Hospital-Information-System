package org.had.hospitalinformationsystem.nurse;


import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NurseRepository extends JpaRepository<Nurse,Long> {
    public Nurse findByUser(User user);
}
