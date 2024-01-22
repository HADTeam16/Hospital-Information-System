package org.had.hospitalinformationsystem.controller;

import org.had.hospitalinformationsystem.model.User;
import org.had.hospitalinformationsystem.repository.UserRepository;
import org.had.hospitalinformationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    //Login
    @PostMapping("/login")
    public String userLogin(@RequestBody User user) throws Exception {
        return  userService.loginUser(user);
    }

    //Get details of all users
    @GetMapping("/allUsers")
    public List<User>getAllUsers(){
        return userRepository.findAll();
    }

    //Get User details by Id
    @GetMapping("/{id}")
    public User findUser(@PathVariable Long id) throws Exception {
        User user;
        user = userService.findUserById(id);
        return  user;

    }
    //Add User Details
    @PostMapping("/createUser")
    public User createUser(@RequestBody User user){
        User savedUser;
        savedUser = userService.registerUser(user);
        return savedUser;
    }

//    @PutMapping("/updateUser/{id}")
//    public User updateUser(@RequestBody User user){
//        try{
//            User newUser = userRepository.findAllById(user.getId());
//        }catch(){
//
//        }
//    }

}
