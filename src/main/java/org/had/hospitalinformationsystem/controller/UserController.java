package org.had.hospitalinformationsystem.controller;

import org.had.hospitalinformationsystem.model.User;
import org.had.hospitalinformationsystem.repository.UserRepository;
import org.had.hospitalinformationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    //Get details of all users
    @GetMapping("/allUsers")
    public List<User>getAllUsers(@RequestHeader("Authorization") String jwt){
        return userRepository.findAll();
    }

    //Get User details by id
    @GetMapping("/userById/{id}")
    public User findUserById(@PathVariable Long id) throws Exception {
        User user;
        user = userService.findUserById(id);
        return  user;
    }

    //Get User Details by Role
    @GetMapping("/userByRole/{role}")
    public List<User> findUserByRole(@PathVariable String role) throws Exception {
        return userService.findUserByRole(role);
    }

    //Add User Details
    @PutMapping("/update")
    public User updateUser(@RequestHeader("Authorization") String jwt,@RequestBody User user){

        User reqUser = userService.findUserByJwt(jwt);

        return userService.updateUser(user, reqUser.getId());
    }

    @GetMapping()
    public User getUserByJwt(@RequestHeader("Authorization") String jwt){
        return userService.findUserByJwt(jwt);
    }


}
