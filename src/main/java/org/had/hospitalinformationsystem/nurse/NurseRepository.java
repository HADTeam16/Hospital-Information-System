package org.had.hospitalinformationsystem.nurse;


import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NurseRepository extends JpaRepository<Nurse,Long> {
    public Nurse findByUser(User user);

    @Query(value = "SELECT n.*, COALESCE(w.ward_count, 0) as ward_count " +
            "FROM Nurse n " +
            "LEFT JOIN ( " +
            "    SELECT nurse_id, COUNT(ward_id) as ward_count " +
            "    FROM Ward " +
            "    GROUP BY nurse_id " +
            ") w ON n.userid = w.nurse_id " +
            "ORDER BY ward_count ASC " +
            "LIMIT 1", nativeQuery = true)
    Nurse findNurseWithLeastWardsAssigned();

}
