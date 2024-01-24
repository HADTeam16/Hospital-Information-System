package org.had.hospitalinformationsystem.repository;

import org.had.hospitalinformationsystem.model.Doctor;
import org.had.hospitalinformationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUserName(String userName);
    public User findByEmail(String email);



    @Query("select u from User u where  u.userName LIKE %:query%")
    public List<Doctor> searchUser(@Param("query") String query);
}
