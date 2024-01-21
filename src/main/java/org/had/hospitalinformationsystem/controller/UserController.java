package org.had.hospitalinformationsystem.controller;

import org.had.hospitalinformationsystem.model.User;
import org.had.hospitalinformationsystem.repository.UserRepository;
import org.had.hospitalinformationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user){
        User savedUser=userService.registerUser(user);
        return savedUser;
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable Long id) throws Exception {
        User user=userService.findUserById(id);
        return  user;

    }
}
