package org.had.hospitalinformationsystem.receptionist;

import org.had.hospitalinformationsystem.appointment.Appointment;

import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.dto.AuthResponse;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.user.UserService;
import org.had.hospitalinformationsystem.utility.Utils;
import org.had.hospitalinformationsystem.ward.WardService;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReceptionistServiceImplementation extends Utils implements ReceptionistService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DoctorRepository doctorRepository;


    @Autowired
    WardService wardService;

    @Autowired
    ReceptionistRepository receptionistRepository;


    @Qualifier("jasyptStringEncryptor")
    @Autowired
    private StringEncryptor stringEncryptor;

    private final SimpMessagingTemplate messagingTemplate;

    public ReceptionistServiceImplementation(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void sendAppointmentUpdate(Appointment appointment) {
        messagingTemplate.convertAndSend("/topic/appointments", appointment);
    }

    @Override
    public ResponseEntity<Object> signupPatient(String jwt, RegistrationDto registrationDto) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("receptionist")){
                registrationDto.setPassword("");
                Object result = getUser(registrationDto);
                if(result instanceof String){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponse(null,(String) result,null));
                }
                else{
                    User newUser = (User) result;
                    newUser.setDisable(true);
                    newUser.getAuth().setPassword("");
                    Object patientResult = Utils.getPatient(registrationDto,newUser);
                    if (patientResult instanceof String) {
                        return ResponseEntity.badRequest().body(new AuthResponse(null, (String) patientResult, null));
                    } else {
                        Patient newPatient = (Patient) patientResult;
                        userRepository.save(newUser);
                        newPatient.setBloodGroup(stringEncryptor.encrypt(((Patient) patientResult).getBloodGroup()));
                        newPatient.setHeight(stringEncryptor.encrypt(((Patient) patientResult).getHeight()));
                        newPatient.setBloodPressure(stringEncryptor.encrypt(((Patient) patientResult).getBloodPressure()));
                        patientRepository.save(newPatient);
                    }
                    return ResponseEntity.ok(new AuthResponse("", "Register Success", newUser));
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null,"Access denied",null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during patient registration: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> findDoctorBySpecialization(String jwt, String specialization) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (!role.equals("receptionist")) {
                throw new Exception("Access Denied - Only receptionists can access this information.");
            }

            List<Doctor> doctors = doctorRepository.findDoctorBySpecialization(specialization);
            if (doctors.isEmpty()) {
                throw new Exception("Doctor with the specialization '" + specialization + "' does not exist.");
            }

            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error finding doctor by specialization: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> createWard(String jwt){
        String role=JwtProvider.getRoleFromJwtToken(jwt);
        if(role.equals("receptionist")){
            wardService.createInitialWards();
            return ResponseEntity.ok("Wards created successfully");
        }
        else{
            return ResponseEntity.badRequest().body("Only Receptionist can create wards");
        }
    }

    @Override
    public ResponseEntity<List<Receptionist>> getAllReceptionist(String jwt){
        String userName=JwtProvider.getUserNameFromJwtToken(jwt);
        User user=userRepository.findByUserName(userName);
        if(user.getRole().equals("admin")){
            List<Receptionist> receptionists=receptionistRepository.findAll();
            return ResponseEntity.ok().body(receptionists);
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    public Boolean removeConsentForPatientId(String jwt,String emailId){
        try{
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("receptionist")) {
                Patient patient = patientRepository.findPatientByEmailId(emailId);
                patient.setConsent(false);
                patientRepository.save(patient);
                return true;
            }
            else {
                return false;
            }
        }catch(Exception e){
            return false;
        }
    }

    public Boolean giveConsentForPatientId(String jwt,String emailId){
        try{
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("receptionist")) {
                Patient patient = patientRepository.findPatientByEmailId(emailId);
                if(patient !=null) {
                    patient.setConsent(true);
                    patientRepository.save(patient);
                }
                return true;
            }
            else {
                return false;
            }
        }catch(Exception e){
            return false;
        }
    }

    public  Boolean checkPatientByPatientId(String jwt,Long id){
        try{
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if(role.equals("doctor") || role.equals("nurse") || role.equals("receptionist") ){
                return patientRepository.findPatientById(id).isConsent();
            }
            else{
                return  false;
            }
        }
        catch(Exception e){
            return  false;
        }
    }
}
