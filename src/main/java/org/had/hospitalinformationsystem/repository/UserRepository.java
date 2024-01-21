package org.had.hospitalinformationsystem.repository;

import org.had.hospitalinformationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findByUserName(String username);

    @Query("select u from User u where u.firstName LIKE %:query% OR u.lastName LIKE %:query% OR u.userName LIKE %:query%")
    public List<User> searchUser(@Param("query") String query);
}
