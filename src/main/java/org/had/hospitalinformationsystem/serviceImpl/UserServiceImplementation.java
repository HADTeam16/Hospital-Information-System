package org.had.hospitalinformationsystem.serviceImpl;

import org.had.hospitalinformationsystem.config.JwtProvider;
import org.had.hospitalinformationsystem.model.User;
import org.had.hospitalinformationsystem.repository.UserRepository;
import org.had.hospitalinformationsystem.service.UserService;
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


    public String loginUser(User user){

        if(user.getUserName().isEmpty() || user.getPassword().isEmpty()) return "Add userName and/or Password";

        User currUser = userRepository.findByUserName(user.getUserName());
        if(currUser!=null){
            String currUserPassword = currUser.getPassword();
            String userPassword = user.getPassword();
            boolean isPwdRight;
            isPwdRight = currUserPassword.equals(userPassword);
            if(isPwdRight){
                return currUser.getRole();
            }
            else{
                return "Check Username and/or Password";
            }
        }
        return "Check Username and/or Password";
    }

    @Override
    public User registerUser(User user) {
        userRepository.save(user);
        return user;
    }

    @Override
    public User findUserById(Long userId) throws Exception {
        Optional<User> user= userRepository.findById(userId);
        if(user.isPresent()){
            return user.get();
        }
        throw new Exception("user does not exist with userid " + userId);
    }


    @Override
    public List<User> searchUser(String query) {
        return userRepository.searchUser(query);
    }

    @Override
    public User findUserByJwt(String jwt) {
        String userName= JwtProvider.getUserNameFromJwtToken(jwt);
        User user=userRepository.findByUserName(userName);
        return user;
    }

    public List<User> findUserByRole(String role) {
        return userRepository.findAllByRole(role);
    }

    public List<User> findUserBySpecialization(String specialization) {
        return userRepository.findUserBySpecialization(specialization);
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
//
//    @Override
//    public List<User> searchUser(String query) {
//        return userRepository.searchUser(query);
//    }
}
