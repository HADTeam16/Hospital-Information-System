package org.had.hospitalinformationsystem.service;

import org.had.hospitalinformationsystem.model.User;

import java.util.List;

public interface UserService {

    User findUserById(Long userId);

    User updateUser(User user,Long userId);

    List<User> searchUser(String query);
}
