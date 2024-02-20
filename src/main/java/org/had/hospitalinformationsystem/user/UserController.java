package org.had.hospitalinformationsystem.user;

import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @GetMapping("/allUsers")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String jwt) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if ("admin".equals(role)) {
                List<User> users = userRepository.findAll();
                return ResponseEntity.ok(users);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/user/id")
    public ResponseEntity<User> findUserById(@RequestHeader("Authorization") String jwt,@RequestParam Long id) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("admin")) {
                User user = userService.findUserById(id);
                return ResponseEntity.ok(user);
            }
            else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            if (e instanceof NoSuchElementException) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @GetMapping("/user/role")
    public ResponseEntity<List<User>> findUserByRole(@RequestHeader("Authorization") String jwt, @RequestBody String role) {
        try {
            String userRole = JwtProvider.getRoleFromJwtToken(jwt);

            if ("admin".equals(userRole)) {
                List<User> users = userService.findUserByRole(role);
                return ResponseEntity.ok(users);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/valid/username")
    public boolean userPresentOrNot(@RequestHeader("Authorization") String jwt, @RequestBody String userName) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if ("receptionist".equals(role)) {
                User newUser = userRepository.findByUserName(userName);
                return newUser == null;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestHeader("Authorization") String jwt, @RequestBody User user) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);

            if ("admin".equals(role)) {
                User reqUser = userService.findUserByJwt(jwt);

                if (reqUser != null) {
                    User updatedUser = userService.updateUser(user, reqUser.getId());
                    return ResponseEntity.ok(updatedUser);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping()
    public User getUserByJwt(@RequestHeader("Authorization") String jwt){
        return userService.findUserByJwt(jwt);
    }


}
