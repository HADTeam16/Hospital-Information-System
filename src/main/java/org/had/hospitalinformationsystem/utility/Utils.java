package org.had.hospitalinformationsystem.utility;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.had.hospitalinformationsystem.auth.Auth;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.had.hospitalinformationsystem.dto.SmsTwilioConfig;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.user.UserService;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class Utils {


    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender sender;
    @Autowired
    private SmsTwilioConfig smsTwilioConfig;
    @Autowired
    UserService userService;
    @Qualifier("jasyptStringEncryptor")
    @Autowired
    private static StringEncryptor stringEncryptor;

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    private static boolean validBloodGroup(String str){
        String bloodGroupPattern = "(A|B|AB|O)[+-]";
        if(str.matches(bloodGroupPattern)) {
            return true;
        } else {
            return false;
        }
    }

    protected static String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }

    protected static String generateRandomString(int length) {
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


    protected void sendEmail(String email, String username, String password, String name, String subject, String messageTemplate) {
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(subject);
            String message = String.format(messageTemplate, name, username, password);
            helper.setText(message, true);
            sender.send(mimeMessage);
        } catch (Exception ignored) {
        }
    }

    protected void sendSms(PhoneNumber to, String otpMessage){
        try{
            PhoneNumber from = new PhoneNumber(smsTwilioConfig.getTrialNumber());
            Message.creator(to, from, otpMessage).create();
        }
        catch(Exception ignored){
        }
    }

    protected static boolean verifyPassword(String providedPassword, String storedPasswordHash, String salt) {
        String newHash = hashPassword(providedPassword, salt);
        return newHash.equals(storedPasswordHash);
    }

    protected static String hashPassword(String password, String salt) {
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

    protected Object getUser(RegistrationDto registrationDto) {
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
        if(registrationDto.getEmergencyContactName().equals(null)){
            if(registrationDto.getEmergencyContactNumber().equals(null)){
                return "Please, Enter Valid Emergency Contact Number";
            }
            else if(registrationDto.getEmergencyContactNumber().length()!=10){
                return "Please Enter a valid Emergency Valid Contact Number";
            }
            else if(!registrationDto.getEmergencyContactNumber().matches("\\d+")){
                return "Emergency Contact number must contain digits only";
            }
        }

        User newUser = new User();
        Auth auth = new Auth();
        newUser.setUserName(userService.generateUsername(registrationDto.getFirstName()));
        //newUser.setUserName(registrationDto.getUserName());
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
                registrationDto.getMedicalDegree(),
                registrationDto.getMedicalLicenseNumber(),
                registrationDto.getSpecialization()
        };
        String[] fieldNames = {
                "Board Certification",
                "CV",
                "Drug Screening Result",
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
        newDoctor.setMedicalDegree(registrationDto.getMedicalDegree());
        newDoctor.setMedicalLicenseNumber(registrationDto.getMedicalLicenseNumber());
        newDoctor.setSpecialization(registrationDto.getSpecialization());
        newDoctor.setWorkStart(registrationDto.getWorkStart());
        newDoctor.setWorkEnd(registrationDto.getWorkEnd());
        return newDoctor;
    }

    public static Object getPatient(RegistrationDto registrationDto, User newUser) {
        String[] fields = {
                String.valueOf(registrationDto.getTemperature()),
                registrationDto.getBloodPressure(),
                String.valueOf(registrationDto.getHeartRate()),
                String.valueOf(registrationDto.getWeight()),
                registrationDto.getHeight(),
                registrationDto.getBloodGroup(),
        };
        String[] fieldNames = {
                "Temperature",
                "Blood Pressure",
                "Heart Rate",
                "Weight",
                "Height",
                "Blood Group",
        };


        for (int i = 0; i < fields.length; i++) {
            if (fields[i] == null) {
                return fieldNames[i] + " is missing";
            }
        }

        Patient newPatient = new Patient();
        newPatient.setUser(newUser);
        newPatient.setTemperature(registrationDto.getTemperature());
        newPatient.setBloodPressure(registrationDto.getBloodPressure());
        newPatient.setHeartRate(registrationDto.getHeartRate());
        newPatient.setWeight(registrationDto.getWeight());
        newPatient.setRegistrationDateAndTime(LocalDateTime.now());
        newPatient.setConsent(true);
        newPatient.setHeight(registrationDto.getHeight());
        newPatient.setBloodGroup(registrationDto.getBloodGroup());
        return newPatient;
    }
}