package org.had.hospitalinformationsystem.user;

import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.dto.AuthResponse;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.nurse.Nurse;
import org.had.hospitalinformationsystem.nurse.NurseRepository;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    NurseRepository nurseRepository;

    @Autowired
    DoctorRepository doctorRepository;

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

    @GetMapping("/get/user/{userId}")
    public ResponseEntity<RegistrationDto>getUser(@RequestHeader("Authorization") String jwt, @PathVariable Long userId){
        try{
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("admin")){
                RegistrationDto ans = new RegistrationDto();
                Optional<User> optionalUser = userRepository.findById(userId);
                if(optionalUser.isPresent()){
                    User user = optionalUser.get();
                    ans.setFirstName(user.getFirstName());
                    ans.setMiddleName(user.getMiddleName());
                    ans.setLastName(user.getLastName());
                    ans.setAge(user.getAge());
                    ans.setGender(user.getGender());
                    ans.setDateOfBirth(user.getDateOfBirth());
                    ans.setCountry(user.getCountry());
                    ans.setState(user.getState());
                    ans.setCity(user.getCity());
                    ans.setAddressLine1(user.getAddressLine1());
                    ans.setAddressLine2(user.getAddressLine2());
                    ans.setLandmark(user.getLandmark());
                    ans.setPinCode(user.getPinCode());
                    ans.setContact(user.getContact());
                    ans.setProfilePicture(user.getProfilePicture());
                    ans.setEmergencyContactName(user.getEmergencyContactName());
                    ans.setEmergencyContactNumber(user.getEmergencyContactNumber());
                    switch (user.getRole()) {
                        case "doctor" -> {
                            Doctor doctor = doctorRepository.findByUser(user);
                            if (doctor != null) {
                                ans.setMedicalLicenseNumber(doctor.getMedicalLicenseNumber());
                                ans.setSpecialization(doctor.getSpecialization());
                                ans.setExperience(doctor.getExperience());
                                ans.setWorkStart(doctor.getWorkStart());
                                ans.setWorkEnd(doctor.getWorkEnd());

                            }
                        }
                        case "receptionist" -> {
                            // Update receptionist specific details if any
                        }
                        case "nurse" -> {
                            Nurse nurse = nurseRepository.findByUser(user);
                            if (nurse != null) {
                                ans.setHeadNurse(nurse.isHeadNurse());
                            }
                        }
                    }
                    return ResponseEntity.ok(ans);
                }
                else{
                    return ResponseEntity.notFound().build();
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    @PutMapping("/update/user/{userId}")
    public ResponseEntity<AuthResponse> updateUser(@RequestHeader("Authorization") String jwt, @PathVariable Long userId, @RequestBody RegistrationDto registrationDto) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("admin")) {
                Optional<User> optionalUser = userRepository.findById(userId);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    user.setFirstName(registrationDto.getFirstName());
                    user.setMiddleName(registrationDto.getMiddleName());
                    user.setLastName(registrationDto.getLastName());
                    user.setAge(registrationDto.getAge());
                    user.setGender(registrationDto.getGender());
                    user.setDateOfBirth(registrationDto.getDateOfBirth());
                    user.setCountry(registrationDto.getCountry());
                    user.setState(registrationDto.getState());
                    user.setCity(registrationDto.getCity());
                    user.setAddressLine1(registrationDto.getAddressLine1());
                    user.setAddressLine2(registrationDto.getAddressLine2());
                    user.setLandmark(registrationDto.getLandmark());
                    user.setPinCode(registrationDto.getPinCode());
                    user.setContact(registrationDto.getContact());
                    user.setProfilePicture(registrationDto.getProfilePicture());
                    user.setEmergencyContactName(registrationDto.getEmergencyContactName());
                    user.setEmergencyContactNumber(registrationDto.getEmergencyContactNumber());

                    switch (user.getRole()) {
                        case "doctor" -> {
                            Doctor doctor = doctorRepository.findByUser(user);
                            if (doctor != null) {
                                doctor.setSpecialization(registrationDto.getSpecialization());
                                doctor.setWorkStart(registrationDto.getWorkStart());
                                doctor.setWorkEnd(registrationDto.getWorkEnd());
                                doctor.setMedicalLicenseNumber(registrationDto.getMedicalLicenseNumber());
                                doctor.setExperience(registrationDto.getExperience());
                                doctorRepository.save(doctor);
                            }
                        }
                        case "receptionist" -> {
                            // Update receptionist specific details if any
                        }
                        case "nurse" -> {
                            Nurse nurse = nurseRepository.findByUser(user);
                            if (nurse != null) {
                                nurse.setHeadNurse(registrationDto.isHeadNurse());
                                nurseRepository.save(nurse);
                            }
                        }
                    }
                    // Save updated user
                    User savedUser = userRepository.save(user);
                    return ResponseEntity.ok(new AuthResponse("", "User updated successfully", savedUser));
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("", "Access Denied", null));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse("",  e.getMessage() + "Error updating user", null));
        }

    }

}
