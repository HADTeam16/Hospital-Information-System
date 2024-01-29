package org.had.hospitalinformationsystem.service;

import org.had.hospitalinformationsystem.model.User;

import java.util.List;

public interface UserService {

    String loginUser(User user) throws Exception;


    User registerUser(User user);

    List<User> findUserByRole(String role) throws Exception;

    List<User> findUserBySpecialization(String specialization) throws Exception;


    User findUserById(Long userId) throws Exception;




    List<User> searchUser(String  query);
    User findUserByJwt(String jwt);

    User updateUser(User user, Long userId);
}
