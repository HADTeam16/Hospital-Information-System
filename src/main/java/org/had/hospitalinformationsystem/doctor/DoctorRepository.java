package org.had.hospitalinformationsystem.doctor;

import org.had.hospitalinformationsystem.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {

    public Doctor findByuser(User user);
}
