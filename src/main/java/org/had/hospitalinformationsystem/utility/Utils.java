package org.had.hospitalinformationsystem.utility;

import org.had.hospitalinformationsystem.auth.Auth;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.had.hospitalinformationsystem.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Service
public class Utils {

    @Autowired
    PasswordEncoder passwordEncoder;
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }

    public User getUser(RegistrationDto registrationDto) {
        User newUser = new User();
        Auth auth = new Auth();
        newUser.setUserName(registrationDto.getUserName());
        String salt = generateRandomString(27);
        newUser.setSalt(salt);
        auth.setPassword(hashPassword(registrationDto.getPassword(),salt));
        newUser.setAuth(auth);
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
        newUser.setDisable(true);
        return newUser;
    }

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    public static boolean verifyPassword(String providedPassword, String storedPasswordHash, String salt) {
        String newHash = hashPassword(providedPassword, salt);
        return newHash.equals(storedPasswordHash);
    }

    public static String hashPassword(String password, String salt) {
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = Base64.getDecoder().decode(salt);

        PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, ITERATIONS, KEY_LENGTH);

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
