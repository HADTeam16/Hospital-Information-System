package org.had.hospitalinformationsystem.user;

import org.had.hospitalinformationsystem.auth.JwtProvider;
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
    public User findUserById(Long userId) throws Exception {
        Optional<User> user= userRepository.findById(userId);
        if(user.isPresent()){
            return user.get();
        }
        throw new Exception("user does not exist with userid " + userId);
    }

    @Override
    public User findUserByJwt(String jwt) {
        String userName= JwtProvider.getUserNameFromJwtToken(jwt);
        return userRepository.findByUserName(userName);
    }

    public List<User> findUserByRole(String role) {
        return userRepository.findAllByRole(role);
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
}