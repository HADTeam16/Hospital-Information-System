package org.had.hospitalinformationsystem.controller;

import org.had.hospitalinformationsystem.model.User;
import org.had.hospitalinformationsystem.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
public class UserController {

    public UserRepository userRepository;

    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

//    @PostMapping("/create")
//    public String createUser(@RequestBody User user) throws InterruptedException, ExecutionException{
//        return userRepository.createUser(user);
//    }

    @GetMapping("/get/{username}")
    public User getUser(@PathVariable String username) throws InterruptedException, ExecutionException{
        return userRepository.getUser(username);
    }

//    @GetMapping("/test")
//    public ResponseEntity<String> testGetEndpoints(){
//        return ResponseEntity.ok("Working!!!");
//    }

}
