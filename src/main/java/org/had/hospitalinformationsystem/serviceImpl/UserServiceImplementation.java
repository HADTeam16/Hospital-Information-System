package org.had.hospitalinformationsystem.serviceImpl;

import org.had.hospitalinformationsystem.model.User;
import org.had.hospitalinformationsystem.repository.UserRepository;
import org.had.hospitalinformationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {
    @Autowired
    UserRepository userRepository;
    @Override
    public User registerUser(User user) {
        User newUser=new User();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setUserName(user.getUserName());
        newUser.setPassword(user.getPassword());
        newUser.setAge(user.getAge());
        newUser.setRole(user.getRole());
        newUser.setContact(user.getContact());
        newUser.setAvatarUrl(user.getAvatarUrl());
        newUser.setSpecialization(user.getSpecialization());

        userRepository.save(newUser);
        return newUser;
    }

    @Override
    public User findUserById(Long userId) throws Exception {
        Optional<User> user= userRepository.findById(userId);
        if(user.isPresent()){
            return user.get();
        }
        throw new Exception("user does not exist with userid " + userId);
    }

    @Override
    public User findUserByUsername(String username) throws Exception {
        Optional<User> user= userRepository.findByUserName(username);

        if(user.isPresent()){
            return user.get();
        }
        throw new Exception("user does not exist with this  username -> "+ username);
    }


    @Override
    public User updateUser(User user, Long userId) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist with id " + userId));
        if (user.getFirstName() != null) {
            oldUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            oldUser.setLastName(user.getLastName());
        }

        return userRepository.save(oldUser);
    }

    @Override
    public List<User> searchUser(String query) {
        return userRepository.searchUser(query);
    }
}
