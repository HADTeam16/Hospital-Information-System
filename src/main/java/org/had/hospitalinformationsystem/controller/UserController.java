package org.had.hospitalinformationsystem.controller;

import org.had.hospitalinformationsystem.model.User;
import org.had.hospitalinformationsystem.repository.UserRepository;
import org.had.hospitalinformationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public String userLogin(@RequestBody User user) throws Exception {
        return  userService.loginUser(user);
    }



    @PostMapping("/createUser")
    public User createUser(@RequestBody User user){
        User savedUser;
        savedUser = userService.registerUser(user);
        return savedUser;
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable Long id) throws Exception {
        User user;
        user = userService.findUserById(id);
        return  user;

    }
}
