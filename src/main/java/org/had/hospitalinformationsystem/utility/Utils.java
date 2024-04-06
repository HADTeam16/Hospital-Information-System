package org.had.hospitalinformationsystem.utility;

import org.had.hospitalinformationsystem.auth.Auth;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalTime;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Utils {
    @Autowired
    CustomerUserDetailsServiceImplementation customerUserDetailsService;
    @Autowired
    UserRepository userRepository;
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

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public Object getUser(RegistrationDto registrationDto) {
        if (registrationDto == null) {
            return "Registration data is missing";
        }
        String[] fields = {
                registrationDto.getFirstName(),
                registrationDto.getGender(),
                registrationDto.getDateOfBirth(),
                registrationDto.getCountry(),
                registrationDto.getState(),
                registrationDto.getCity(),
                registrationDto.getAddressLine1(),
                registrationDto.getPinCode(),
                registrationDto.getContact(),
                registrationDto.getEmail(),
                registrationDto.getProfilePicture(),
                registrationDto.getRole()
        };
        String[] fieldNames = {
                "First Name",
                "Gender",
                "Date of Birth",
                "Country",
                "State",
                "City",
                "Address Line 1",
                "Pin Code",
                "Contact",
                "Email",
                "Profile Picture",
                "Role"
        };

        for (int i = 0; i < fields.length; i++) {
            if (fields[i] == null) {
                return fieldNames[i] + " is missing";
            }
        }
        String gender = registrationDto.getGender();
        if(!gender.equalsIgnoreCase("male") && !gender.equalsIgnoreCase("female") && !gender.equals("notSpecified")){
            return "Select Gender form the given value:(male,female,notSpecified)";
        }
        String pinCode = registrationDto.getPinCode();
        if(pinCode.length()!=6){
            return "Please, Enter Valid Pin code";
        }
        if (!pinCode.matches("\\d+")) {
            return "Pin code must contain digits only";
        }
        String contact = registrationDto.getContact();
        if(contact.length()!=10){
            return "Please, Enter Valid Contact Number";
        }
        if (!contact.matches("\\d+")) {
            return "Contact number must contain digits only";
        }
        if(!isValidEmail(registrationDto.getEmail())){
            return "Please, Enter Valid Email";
        }


        User newUser = new User();
        Auth auth = new Auth();
        newUser.setUserName(registrationDto.getUserName());
        String salt = generateRandomString(27);
        auth.setSalt(salt);
        auth.setPassword(hashPassword(registrationDto.getPassword(), salt));
        newUser.setAuth(auth);
        newUser.setFirstName(registrationDto.getFirstName());
        newUser.setMiddleName(registrationDto.getMiddleName());
        newUser.setLastName(registrationDto.getLastName());
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

    public static Object getDoctor(RegistrationDto registrationDto, User newUser) {
        String[] fields = {
                registrationDto.getBoardCertification(),
                registrationDto.getCv(),
                registrationDto.getDrugScreeningResult(),
                registrationDto.getExperience(),
                registrationDto.getMedicalDegree(),
                registrationDto.getMedicalLicenseNumber(),
                registrationDto.getSpecialization()
        };
        String[] fieldNames = {
                "Board Certification",
                "CV",
                "Drug Screening Result",
                "Experience",
                "Medical Degree",
                "Medical License Number",
                "Specialization",
        };
        LocalTime[] fields2= {
                registrationDto.getWorkStart(),
                registrationDto.getWorkEnd()
        };
        String[] fieldNames2 = {
                "Work Start",
                "Work End"
        };

        for (int i = 0; i < fields.length; i++) {
            if (fields[i] == null) {
                return fieldNames[i] + " is missing";
            }
        }
        for (int i = 0; i < fields2.length; i++) {
            if (fields2[i] == null) {
                return fieldNames2[i] + " is missing";
            }
        }

        Doctor newDoctor = new Doctor();
        newDoctor.setUser(newUser);
        newDoctor.setBoardCertification(registrationDto.getBoardCertification());
        newDoctor.setCv(registrationDto.getCv());
        newDoctor.setDrugScreeningResult(registrationDto.getDrugScreeningResult());
        newDoctor.setExperience(registrationDto.getExperience());
        newDoctor.setMedicalDegree(registrationDto.getMedicalDegree());
        newDoctor.setMedicalLicenseNumber(registrationDto.getMedicalLicenseNumber());
        newDoctor.setSpecialization(registrationDto.getSpecialization());
        newDoctor.setWorkStart(registrationDto.getWorkStart());
        newDoctor.setWorkEnd(registrationDto.getWorkEnd());
        return newDoctor;
    }



    public Authentication authenticate(String userName, String password, String role) {
        UserDetails userDetails=customerUserDetailsService.loadUserByUsername(userName);
        User currUser = userRepository.findByUserName(userName);
        if(userDetails==null){
            throw new BadCredentialsException("Invalid Username or password");
        }

        String salt = currUser.getAuth().getSalt();
        if(!Utils.verifyPassword(password,userDetails.getPassword(),salt)){
            throw new BadCredentialsException("Invalid Username or password");
        }
        User user=userRepository.findByUserName(userDetails.getUsername());
        if(!user.getRole().matches(role)){
            throw new BadCredentialsException("Invalid Username or password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }
}

