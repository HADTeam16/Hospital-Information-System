package org.had.hospitalinformationsystem.user;

import org.had.hospitalinformationsystem.dto.AuthResponse;
import org.had.hospitalinformationsystem.dto.HospitalLiveStatsDto;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {


    ResponseEntity<List<User>> getAllUsers(String jwt);

    ResponseEntity<User> findUserById(String jwt,Long id);

    ResponseEntity<List<User>> findUserByRole(String jwt, String role);

    boolean userPresentOrNot(String jwt, String userName);

    ResponseEntity<User> updateUser(String jwt, User user);

    ResponseEntity<?>getUser(String jwt, Long userId);

    ResponseEntity<AuthResponse> updateUser(String jwt, Long userId, RegistrationDto registrationDto);

    User findUserByJwt(String jwt);

    User updateUser(User user, Long userId);
    HospitalLiveStatsDto getHospitalStats(String jwt);

    public String generateUsername(String firstName);
}
