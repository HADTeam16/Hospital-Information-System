package org.had.hospitalinformationsystem.service;

import org.had.hospitalinformationsystem.model.User;

import java.util.List;

public interface UserService {

    User registerUser(User user);

    User findUserById(Long userId) throws Exception;

    User findUserByUsername(String username) throws Exception;


    User updateUser(User user, Long userId);

    List<User> searchUser(String  query);
}
