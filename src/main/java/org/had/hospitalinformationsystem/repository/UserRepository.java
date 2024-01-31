package org.had.hospitalinformationsystem.repository;

import org.had.hospitalinformationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String username);

    @Query("SELECT u FROM User u WHERE u.role = ?1")
    List<User> findAllByRole(String role);

    @Query("select u from User u where u.firstName LIKE %:query% OR u.lastName LIKE %:query% OR u.userName LIKE %:query%")
    List<User> searchUser(@Param("query") String query);
}
