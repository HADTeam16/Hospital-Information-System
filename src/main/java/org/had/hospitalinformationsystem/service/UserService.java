package org.had.hospitalinformationsystem.service;

import org.had.hospitalinformationsystem.model.User;

import java.util.List;

public interface UserService {


    List<User> findUserByRole(String role) throws Exception;

    User findUserById(Long userId) throws Exception;

    User findUserByJwt(String jwt);

    User updateUser(User user, Long userId);
}
