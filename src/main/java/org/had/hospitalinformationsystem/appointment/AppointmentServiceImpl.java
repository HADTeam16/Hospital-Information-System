package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.dto.AppointmentDataDto;
import org.had.hospitalinformationsystem.dto.AppointmentDto;
import org.had.hospitalinformationsystem.dto.AppointmentResponseDto;
import org.had.hospitalinformationsystem.dto.PrescriptionsAndRecords;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.prescription.PrescriptionRepository;
import org.had.hospitalinformationsystem.records.Records;
import org.had.hospitalinformationsystem.records.RecordsRepository;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentServiceImpl extends AppointmentUtils implements AppointmentService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RecordsRepository recordsRepository;
    @Autowired
    PrescriptionRepository prescriptionRepository;
    @Autowired
    PatientRepository patientRepository;
    @Qualifier("jasyptStringEncryptor")
    @Autowired
    private StringEncryptor stringEncryptor;

    @Override
    public ResponseEntity<List<Appointment>> getAllAppointments(String jwt) {

        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            System.out.println(role);
            if (role.equals("receptionist")) {
                List<Appointment> appointments = appointmentRepository.findAllAppointment();
                for(Appointment a:appointments){
                    a.setPurpose(stringEncryptor.decrypt(a.getPurpose()));
                    a.setBloodPressure(stringEncryptor.decrypt(a.getBloodPressure()));
                }
                return ResponseEntity.ok(appointments);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    public ResponseEntity<List<Appointment>> getAllAppointmentsByDate(String jwt, LocalDate date) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            String userName = JwtProvider.getUserNameFromJwtToken(jwt);
            User user = userRepository.findByUserName(userName);
            if (role.equals("doctor")) {
                LocalDateTime startDate = date.atStartOfDay();
                LocalDateTime endDate = startDate.plusDays(1);
                List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentDate(user.getId(), startDate, endDate);
                for(Appointment a:appointments){

                    a.setPurpose(stringEncryptor.decrypt(a.getPurpose()));
                    a.setBloodPressure(stringEncryptor.decrypt(a.getBloodPressure()));
                }

                return ResponseEntity.ok(appointments);

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @Override
    public ResponseEntity<?> getDoctorsAppointments(String jwt) {
        try {
            String userName = JwtProvider.getUserNameFromJwtToken(jwt);
            User user = userRepository.findByUserName(userName);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            Doctor doctor = doctorRepository.findByUser(user);
            if (doctor == null) {
                return ResponseEntity.badRequest().body("Doctor not found for user: " + userName);
            }

            List<Patient> appointments = appointmentRepository.getDoctorsAppointment(doctor.getDoctorId());

            return ResponseEntity.ok(appointments);
        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body("An error occurred while fetching appointments: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> bookAppointment(String jwt, AppointmentDto appointmentDto) {
        AppointmentResponseDto appointmentResponseDto = new AppointmentResponseDto();
        String userName = JwtProvider.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUserName(userName);

        if (!user.getRole().equals("receptionist")) {
            appointmentResponseDto.setResponse("Only receptionist can book an appointment");
            return ResponseEntity.badRequest().body(appointmentResponseDto);
        }

        try {
            Appointment appointment = createAppointment(appointmentDto); // Assume this method exists and correctly
                                                                         // creates an appointment
            notifyDoctor(appointment); // Send WebSocket notification to the doctor
            appointmentResponseDto
                    .setResponse("Appointment created successfully for: " + appointment.getSlot().toString());
            return ResponseEntity.ok().body(appointmentResponseDto);
        } catch (Exception e) {
            appointmentResponseDto.setResponse("Failed to create appointment: " + e.getMessage());
            return ResponseEntity.badRequest().body(appointmentResponseDto);
        }
    }

    @Override
    public ResponseEntity<?> getAllPreviousAppointmentsForPatient(String jwt, Long patientId, LocalDateTime date) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (!role.equals("doctor")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
            }

            List<Object[]> appointments = appointmentRepository.findAllPreviousAppointmentForPatient(patientId, date);
            List<AppointmentDataDto> appointmentDataDtos = new ArrayList<>();
            for (Object[] appointment : appointments) {
                AppointmentDataDto dto = new AppointmentDataDto();
                dto.setAppointmentId((Long) appointment[0]); // Assuming appointmentId is at index 0
                dto.setDateTime((LocalDateTime) appointment[1]); // Assuming dateTime is at index 1
                appointmentDataDtos.add(dto);
            }
            return ResponseEntity.ok(appointmentDataDtos);
        } catch (Exception e) {
            // Log the exception for debugging
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<PrescriptionsAndRecords> getAppointmentDetails(String jwt, Long appointmentId) {
        String role = JwtProvider.getRoleFromJwtToken(jwt);

        if (role.equals("doctor")) {
            List<String> records = recordsRepository.findRecordsImageByAppointmentId(appointmentId);
            List<String> Record=new ArrayList<>();
            for(String r:records){
                Record.add(stringEncryptor.decrypt(r));
            }
            String prescription = stringEncryptor.decrypt(prescriptionRepository.findPrescriptionImageByAppointmentID(appointmentId));
            Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId);
            appointment.setBloodPressure(stringEncryptor.decrypt(appointment.getBloodPressure()));
            PrescriptionsAndRecords appointmentDetails = new PrescriptionsAndRecords(Record, prescription,
                    appointment);
            return ResponseEntity.ok(appointmentDetails);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> cancelAppointment(String jwt, Long appointmentId) {
        Map<String, String> response = new HashMap<>();
        String role = JwtProvider.getRoleFromJwtToken(jwt);
        if (!role.equals("doctor")) {
            response.put("message", "Only doctor can cancel the appointment");
            return ResponseEntity.badRequest().body(response);
        }
        String userName = JwtProvider.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUserName(userName);
        Optional<Doctor> doctor = doctorRepository.findById(user.getId());
        if (doctor.isPresent()) {
            Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId);
            if (appointment.getDoctor().getDoctorId() != doctor.get().getDoctorId()) {
                response.put("message", "Only appointed doctor can cancel their appointment");
                return ResponseEntity.badRequest().body(response);
            }
            appointment.setCompleted(-1);
            appointmentRepository.save(appointment);
            sendEmailForAckOfAppointmentCancel(appointment.getPatient().getUser().getEmail(),appointment.getPatient().getUser().getUserName(),appointment.getPatient().getUser().getFirstName(),appointment.getSlot());
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok().body(response);
        } else {
            response.put("message", "No doctor found");
            return ResponseEntity.badRequest().body(response);
        }
    }

}
