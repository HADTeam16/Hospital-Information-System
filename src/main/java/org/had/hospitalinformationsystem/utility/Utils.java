package org.had.hospitalinformationsystem.utility;

import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.had.hospitalinformationsystem.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Utils {

    @Autowired
    PasswordEncoder passwordEncoder;

    public User getUser(RegistrationDto registrationDto) {
        User newUser = new User();
        newUser.setUserName(registrationDto.getUserName());
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        newUser.setFirstName(registrationDto.getFirstName());
        newUser.setMiddleName(registrationDto.getMiddleName());
        newUser.setLastName(registrationDto.getLastName());
        newUser.setAge(registrationDto.getAge());
        newUser.setGender(registrationDto.getGender());
        newUser.setDateOfBirth(registrationDto.getDateOfBirth());
        newUser.setCountry(registrationDto.getCountry());
        newUser.setState(registrationDto.getState());
        newUser.setCity(registrationDto.getCity());
        newUser.setAddressLine1(registrationDto.getAddressLine1());
        newUser.setAddressLine2(registrationDto.getAddressLine2());
        newUser.setLandmark(registrationDto.getLandmark());
        newUser.setPinCode(registrationDto.getPinCode());
        newUser.setContact(registrationDto.getContact());
        newUser.setEmail(registrationDto.getEmail());
        newUser.setProfilePicture(registrationDto.getProfilePicture());
        newUser.setEmergencyContactName(registrationDto.getEmergencyContactName());
        newUser.setEmergencyContactNumber(registrationDto.getEmergencyContactNumber());
        newUser.setRole(registrationDto.getRole());
        return newUser;
    }

}
