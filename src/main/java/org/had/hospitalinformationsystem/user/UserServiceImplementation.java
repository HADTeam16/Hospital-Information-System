package org.had.hospitalinformationsystem.user;

import org.had.hospitalinformationsystem.OT.OTRepository;
import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.doctor.DoctorService;
import org.had.hospitalinformationsystem.dto.AuthResponse;
import org.had.hospitalinformationsystem.dto.HospitalLiveStatsDto;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.nurse.Nurse;
import org.had.hospitalinformationsystem.nurse.NurseRepository;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.receptionist.Receptionist;
import org.had.hospitalinformationsystem.receptionist.ReceptionistRepository;
import org.had.hospitalinformationsystem.ward.WardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImplementation implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    OTRepository otRepository;
    @Autowired
    WardRepository wardRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    DoctorService doctorService;
    @Autowired
    NurseRepository nurseRepository;
    @Autowired
    ReceptionistRepository receptionistRepository;

    private User findUserById(Long userId) throws Exception {
        Optional<User> user= userRepository.findById(userId);
        if(user.isPresent()){
            return user.get();
        }
        throw new Exception("user does not exist with userid " + userId);
    }

    @Override
    public ResponseEntity<List<User>> getAllUsers(String jwt) {
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

    @Override
    public ResponseEntity<User> findUserById(String jwt,Long id) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("admin")) {
                User user = findUserById(id);
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

    @Override
    public ResponseEntity<List<User>> findUserByRole(String jwt, String role) {
        try {
            String userRole = JwtProvider.getRoleFromJwtToken(jwt);

            if ("admin".equals(userRole)) {
                List<User> users = findUserByRole(role);
                return ResponseEntity.ok(users);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public boolean userPresentOrNot(String jwt, String userName) {
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

    @Override
    public ResponseEntity<?>getUser(String jwt, Long userId){
        try{
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("admin")){
                User ans = new User();
                Optional<User> optionalUser = userRepository.findById(userId);
                if(optionalUser.isPresent()){
                    User user = optionalUser.get();
                    switch (user.getRole()) {
                        case "doctor" -> {
                            Doctor doctor = doctorRepository.findByUser(user);
                            return ResponseEntity.ok(doctor);
                        }
                        case "receptionist" -> {
                            Receptionist receptionist = receptionistRepository.findByUser(user);
                            return ResponseEntity.ok(receptionist);
                        }
                        case "nurse" -> {
                            Nurse nurse = nurseRepository.findByUser(user);
                            if (nurse != null) {
                                return ResponseEntity.ok(nurse);
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

    @Override
    public ResponseEntity<AuthResponse> updateUser(String jwt, Long userId, RegistrationDto registrationDto) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("admin")) {
                Optional<User> optionalUser = userRepository.findById(userId);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    if(!registrationDto.getFirstName().equals(null)) user.setFirstName(registrationDto.getFirstName());
                    if(!registrationDto.getMiddleName().equals(null))user.setMiddleName(registrationDto.getMiddleName());
                    if(!registrationDto.getLastName().equals(null)) user.setLastName(registrationDto.getLastName());
                    if(!registrationDto.getGender().equals(null)) user.setGender(registrationDto.getGender());
                    if(!registrationDto.getDateOfBirth().equals(null)) user.setDateOfBirth(registrationDto.getDateOfBirth());
                    if(!registrationDto.getCountry().equals(null)) user.setCountry(registrationDto.getCountry());
                    if(!registrationDto.getState().equals(null)) user.setState(registrationDto.getState());
                    if(!registrationDto.getCity().equals(null)) user.setCity(registrationDto.getCity());
                    if(!registrationDto.getAddressLine1().equals(null)) user.setAddressLine1(registrationDto.getAddressLine1());
                    if(!registrationDto.getAddressLine2().equals(null)) user.setAddressLine2(registrationDto.getAddressLine2());
                    if(!registrationDto.getLandmark().equals(null)) user.setLandmark(registrationDto.getLandmark());
                    if(!registrationDto.getPinCode().equals(null)) user.setPinCode(registrationDto.getPinCode());
                    if(!registrationDto.getContact().equals(null)) user.setContact(registrationDto.getContact());
                    if(!registrationDto.getProfilePicture().equals(null)) user.setProfilePicture(registrationDto.getProfilePicture());
                    if(!registrationDto.getEmergencyContactName().equals(null)) user.setEmergencyContactName(registrationDto.getEmergencyContactName());
                    if(!registrationDto.getEmergencyContactName().equals(null)) user.setEmergencyContactNumber(registrationDto.getEmergencyContactNumber());

                    switch (user.getRole()) {
                        case "doctor" -> {
                            Doctor doctor = doctorRepository.findByUser(user);
                            if (doctor != null) {
                                if(!registrationDto.getSpecialization().equals(null)) doctor.setSpecialization(registrationDto.getSpecialization());
                                if(!registrationDto.getWorkStart().equals(null)) doctor.setWorkStart(registrationDto.getWorkStart());
                                if(!registrationDto.getWorkEnd().equals(null)) doctor.setWorkEnd(registrationDto.getWorkEnd());
                                if(!registrationDto.getMedicalLicenseNumber().equals(null)) doctor.setMedicalLicenseNumber(registrationDto.getMedicalLicenseNumber());
                                if(!registrationDto.getBoardCertification().equals(null)) doctor.setBoardCertification(registrationDto.getBoardCertification());
                                if(!registrationDto.getCv().equals(null)) doctor.setCv(registrationDto.getCv());
                                if(!registrationDto.getDrugScreeningResult().equals(null)) doctor.setDrugScreeningResult(registrationDto.getDrugScreeningResult());
                                doctorRepository.save(doctor);
                            }
                        }
                        case "receptionist" -> {
                            // Update receptionist specific details if any
                        }
                        case "nurse" -> {
                            Nurse nurse = nurseRepository.findByUser(user);
                            if (nurse != null) {
                                if(registrationDto.isHeadNurse()) nurse.setHeadNurse(true);
                                if(registrationDto.isHeadNurse()==false) nurse.setHeadNurse(false);
                                nurseRepository.save(nurse);
                            }
                        }
                    }
                    User savedUser = userRepository.save(user);
                    savedUser.setAuth(null);
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

    @Override
    public HospitalLiveStatsDto getHospitalStats(String jwt) {
        String role=JwtProvider.getRoleFromJwtToken(jwt);
        System.out.println(jwt);
        HospitalLiveStatsDto statsDto=new HospitalLiveStatsDto();
        statsDto.setTotalPatientsCount(patientRepository.count());
        statsDto.setCurrentlyScheduledAppointmentCount(appointmentRepository.getScheduledAppointmentCount(LocalDateTime.now()));
        statsDto.setSpecialityWiseDoctorsCount(doctorService.getSpecialityWiseDoctorsCount());
        statsDto.setOtsAvailable(otRepository.findAvailableOt().size());
        statsDto.setTotalOts(10);
        statsDto.setTotalWards(30);
        statsDto.setAvailableWards(wardRepository.findAvailableWard().size());

        String userName=JwtProvider.getUserNameFromJwtToken(jwt);
        User user=userRepository.findByUserName(userName);
        switch(role) {
            case "doctor":
                Optional<Doctor> doctor=doctorRepository.findById(user.getId());
                if(doctor.isPresent()){
                    Long doctorId=doctor.get().getDoctorId();
                    statsDto.setTotalAttendedAppointments(appointmentRepository.getAttendedAppointments(1,doctorId).size());
                    statsDto.setTotalAttendedPatients(appointmentRepository.getDistinctAttendedPatientsCount(1,doctorId));
                    statsDto.setWardsAssignedTillDate(appointmentRepository.getWardAssignedTillDate(doctorId).size());
                }

                break;
            case "nurse":
                Optional<Nurse> nurse=nurseRepository.findById(user.getId());
                if (nurse.isPresent()){
                    Long nurseId=nurse.get().getNurseId();
                    statsDto.setCurrentlyAssignedPatientsCount(wardRepository.assignedPatientsUnderNurse(nurseId).size());
                    statsDto.setTotalWardsAllottedCount(wardRepository.assignedPatientsUnderNurse(nurseId).size());

                }
                break;
            case "receptionist":
                Optional<Receptionist> receptionist=receptionistRepository.findById(user.getId());
                if(receptionist.isPresent()){
                    statsDto.setCurrentlyAvailableSpecialityWiseDoctorsCount(doctorService.getCurrentlyAvailableSpecialityWiseDoctorsCount());
                }
                break;
        }

        return statsDto;

    }

    @Override
    public String generateUsername(String firstName) {
        String prefix = firstName.toLowerCase(); // Normalize the prefix
        List<String> existingUsernames = userRepository.findUsernamesByPrefix(prefix);

        // Find the highest number suffix
        int maxNumber = 0;
        Pattern pattern = Pattern.compile("^" + prefix + "(\\d+)$");
        for (String username : existingUsernames) {
            Matcher matcher = pattern.matcher(username);
            if (matcher.matches()) {
                int number = Integer.parseInt(matcher.group(1));
                if (number > maxNumber) {
                    maxNumber = number;
                }
            }
        }
        return prefix + (maxNumber + 1);
    }

    @Override
    public ResponseEntity<Map<String, String>> updateProfile(String jwt,String profilePic) {
        String userName=JwtProvider.getUserNameFromJwtToken(jwt);
        User user=userRepository.findByUserName(userName);
        user.setProfilePicture(profilePic);
        userRepository.save(user);
        System.out.println(profilePic);
        Map<String, String> response = new HashMap<>();
        response.put("message","Profile Pic Successfull Updated");
        return ResponseEntity.ok().body(response);
    }
}
