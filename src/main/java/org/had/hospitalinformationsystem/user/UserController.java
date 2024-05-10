package org.had.hospitalinformationsystem.user;

import org.had.hospitalinformationsystem.dto.AuthResponse;
import org.had.hospitalinformationsystem.dto.HospitalLiveStatsDto;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {


    @Autowired
    UserService userService;

    @GetMapping("/allUsers")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String jwt) {
        return userService.getAllUsers(jwt);
    }

    @GetMapping("/user/id")
    public ResponseEntity<User> findUserById(@RequestHeader("Authorization") String jwt,@RequestParam Long id) {
        return userService.findUserById(jwt, id);
    }

    @GetMapping("/user/role")
    public ResponseEntity<List<User>> findUserByRole(@RequestHeader("Authorization") String jwt, @RequestBody String role) {
        return userService.findUserByRole(jwt, role);
    }

    @GetMapping("/valid/username/{userName}")
    public boolean userPresentOrNot(@RequestHeader("Authorization") String jwt, @PathVariable String userName) {
        return userService.userPresentOrNot(jwt, userName);
    }

//    @PutMapping("/update")
//    public ResponseEntity<User> updateUser(@RequestHeader("Authorization") String jwt, @RequestBody User user) {
//        return userService.updateUser(jwt, user);
//    }

    @GetMapping()
    public User getUserByJwt(@RequestHeader("Authorization") String jwt){
        return userService.findUserByJwt(jwt);
    }

    @GetMapping("/get/user/{userId}")
    public ResponseEntity<?>getUser(@RequestHeader("Authorization") String jwt, @PathVariable Long userId){
        return userService.getUser(jwt, userId);
    }

    @PutMapping("/update/user/{userId}")
    public ResponseEntity<AuthResponse> updateUser(@RequestHeader("Authorization") String jwt, @PathVariable Long userId, @RequestBody RegistrationDto registrationDto) {
        return userService.updateUser(jwt, userId, registrationDto);
    }

    @GetMapping("/hospital/live/stats")
    public ResponseEntity<HospitalLiveStatsDto> getHospitalStats(@RequestHeader("Authorization") String jwt){
        return ResponseEntity.ok().body(userService.getHospitalStats(jwt));
    }

    @PutMapping("/update/profilepic")
    public ResponseEntity<Map<String,String>> updateProfile(@RequestHeader("Authorization") String jwt,@RequestBody String profilePic){
        return userService.updateProfile(jwt,profilePic);
    }

}
