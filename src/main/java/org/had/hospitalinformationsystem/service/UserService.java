package org.had.hospitalinformationsystem.service;

import org.had.hospitalinformationsystem.model.User;

import java.util.List;

public interface UserService {

    User registerUser(User user);

    User findUserById(Long userId) throws Exception;

    String loginUser(User user) throws Exception;

    User updateUser(User user, Long userId);

    List<User> searchUser(String  query);
}
