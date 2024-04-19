package org.had.hospitalinformationsystem.user;

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


    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.role = 'admin'")
    Boolean findAdminByRole();

    User findUserByEmail(String email);

    @Query("SELECT u.userName FROM User u WHERE u.userName LIKE :prefix%")
    List<String> findUsernamesByPrefix(@Param("prefix") String prefix);

}
