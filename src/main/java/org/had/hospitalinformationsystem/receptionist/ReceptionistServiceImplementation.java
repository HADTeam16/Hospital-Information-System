package org.had.hospitalinformationsystem.receptionist;

import org.had.hospitalinformationsystem.appointment.Appointment;

import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.dto.AuthResponse;
import org.had.hospitalinformationsystem.dto.OtpInfo;
import org.had.hospitalinformationsystem.dto.RegistrationDto;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.prescription.Prescription;
import org.had.hospitalinformationsystem.prescription.PrescriptionRepository;
import org.had.hospitalinformationsystem.records.Records;
import org.had.hospitalinformationsystem.records.RecordsRepository;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.utility.Utils;
import org.had.hospitalinformationsystem.ward.Ward;
import org.had.hospitalinformationsystem.ward.WardRepository;
import org.had.hospitalinformationsystem.ward.WardService;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ReceptionistServiceImplementation extends Utils implements ReceptionistService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    WardRepository wardRepository;
    @Autowired
    WardService wardService;
    @Autowired
    PrescriptionRepository prescriptionRepository;
    @Autowired
    RecordsRepository recordsRepository;
    @Autowired
    ReceptionistRepository receptionistRepository;

    Map<String, OtpInfo> otpMap = new HashMap<>();

    @Autowired
    AppointmentRepository appointmentRepository;
    @Qualifier("jasyptStringEncryptor")
    @Autowired
    private StringEncryptor stringEncryptor;

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

    @Override
    public ResponseEntity<Map<String, String>> deletePatientsendOtp(String jwt, Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);

            if (!role.equals("receptionist")) {
                response.put("Message", "Access Denied");
                return ResponseEntity.badRequest().body(response);
            }
            Ward ward = wardRepository.findByPatient(id);
            if (ward != null) {
                response.put("Message", "Patient is in Ward so data cannot be deleted");
                return ResponseEntity.badRequest().body(response);
            }
            Patient patient = patientRepository.findPatientById(id);
            sendOtpEmailForPatientDataDelete(patient.getUser().getEmail(),patient.getUser().getUserName(),patient.getUser().getFirstName());
            response.put("message", "Otp Sent Successfully!!!");
            return ResponseEntity.ok().body(response);
        }
        catch(Exception e){
            response.put("message","Error!!!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> deletePatientDataValidateOtp(String jwt,Long id, String email, String otp){
        Map<String, String> response = new HashMap<>();
        try{
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("receptionist")) {
                int val = validateOtp(email,otp);
                if(val==1){
                    boolean status = deletePatientRecords(jwt,id);
                    if(status){
                        response.put("message","Patient Data Deleted Successfully");
                    }
                    else{
                        response.put("message","Failed to delete Data Try again!!");
                    }
                    return ResponseEntity.ok(response);
                }
                else if(val==2){
                    response.put("message","OTP has been expired");
                    return ResponseEntity.ok(response);
                }
                else{
                    response.put("message","Invalid OTP");
                    return ResponseEntity.ok(response);
                }
            }
            else {
                response.put("message", "Access denied!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Unknown error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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

    private void sendOtpEmailForPatientDataDelete(String email, String username, String name) {
        String otp = generateOTP();
        String subject = "Deleting Data";
        String messageTemplate = "Dear " + username + ",<br/><br/>" +
                "We have received a request to delete your data associated with the account under the name of " + name + ".<br/><br/>" +
                "To proceed with the deletion, please use the following One Time Password (OTP): <strong>" + otp + "</strong><br/><br/>" +
                "Please note that this OTP is valid for the next 10 minutes. After this time, you will need to generate a new OTP if you wish to proceed with the data deletion process.<br/><br/>" +
                "If you did not initiate this request or have any concerns, please contact our support team immediately.<br/><br/>" +
                "Best regards,<br/>" +
                "Pure Zen Wellness Hospital";
        sendEmail(email, username, "", name, subject, messageTemplate);
        Instant expirationTime = Instant.now().plusSeconds(600);
        otpMap.put(email,new OtpInfo(otp,expirationTime));
    }


    private int validateOtp(String email, String otp){
        String username = email;
        OtpInfo otpInfo = otpMap.get(username);
        if (otpInfo != null && otpInfo.getOtp().equals(otp)){
            if (Instant.now().isBefore(otpInfo.getExpirationTime())) {
                otpMap.remove(username);
                return 1;
            } else {
                otpMap.remove(username);
                return 0;
            }
        } else {
            return -1;
        }
    }

    private boolean deletePatientRecords(String jwt, Long id) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (!role.equals("receptionist")) {
                return false;
            }
            Ward ward = wardRepository.findByPatient(id);
            if (ward != null) {
                return false;
            }
            List<Appointment> appointments = appointmentRepository.getAppointmentByPatientid(id);
            for (Appointment a : appointments) {
                List<Records> records = recordsRepository.findRecordsByAppointmentId(a.getAppointmentId());
                for (Records r : records) {
                    recordsRepository.deleteById(r.getRecordsId());
                }
                Prescription prescription = prescriptionRepository.findPrescriptionByAppointmentID(a.getAppointmentId());
                prescriptionRepository.deleteById(prescription.getPrescriptionId());
                appointmentRepository.deleteById(a.getAppointmentId());
            }
            patientRepository.deleteById(id);
            userRepository.deleteById(id);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}


