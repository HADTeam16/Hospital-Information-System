package org.had.hospitalinformationsystem.appointment;

import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.doctor.DoctorService;

import org.had.hospitalinformationsystem.dto.AppointmentDataDto;
import org.had.hospitalinformationsystem.dto.AppointmentDto;

import org.had.hospitalinformationsystem.dto.AppointmentResponseDto;
import org.had.hospitalinformationsystem.dto.PrescriptionsAndRecords;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.prescription.PrescriptionRepository;
import org.had.hospitalinformationsystem.records.RecordsRepository;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService{

    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    DoctorService doctorService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    RecordsRepository recordsRepository;
    @Autowired
    PrescriptionRepository prescriptionRepository;
    @Override
    public Appointment createAppointment(AppointmentDto appointmentDto) {

        Appointment appointment=new Appointment();
        Doctor doctor=doctorRepository.findById(appointmentDto.getDoctorId()).orElseThrow();

        LocalDateTime nextAvailableSlot=doctorService.findNextAvailableSlot(appointmentDto.getDoctorId());

        if(nextAvailableSlot==null){
            throw new IllegalStateException("No available slots for today");
        }
        Patient patient=patientRepository.findById(appointmentDto.getPatientId()).orElseThrow();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setPurpose(appointmentDto.getPurpose());
        appointment.setSlot(nextAvailableSlot);
        appointment.setNeedWard(false);
        appointment.setCompleted(0);
        appointment.setTemperature(appointmentDto.getTemperature());
        appointment.setBloodPressure(appointmentDto.getBloodPressure());
        appointment.setWeight(appointmentDto.getWeight());
        appointment.setHeight(appointmentDto.getHeight());
        return appointmentRepository.save(appointment);
    }

    @Override
    public ResponseEntity<List<Appointment>> getAllAppointments(String jwt) {
        try {
            String role = JwtProvider.getRoleFromJwtToken(jwt);
            if (role.equals("receptionist")) {
                List<Appointment> appointments = appointmentRepository.findAll();
                return ResponseEntity.ok(appointments);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes
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
                return ResponseEntity.ok(appointmentRepository.findByDoctorIdAndAppointmentDate(user.getId(), startDate, endDate));
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

            return ResponseEntity.internalServerError().body("An error occurred while fetching appointments: " + e.getMessage());
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
            Appointment appointment = createAppointment(appointmentDto); // Assume this method exists and correctly creates an appointment
            notifyDoctor(appointment); // Send WebSocket notification to the doctor
            appointmentResponseDto.setResponse("Appointment created successfully for: " + appointment.getSlot().toString());
            return ResponseEntity.ok().body(appointmentResponseDto);
        } catch (Exception e) {
            appointmentResponseDto.setResponse("Failed to create appointment: " + e.getMessage());
            return ResponseEntity.badRequest().body(appointmentResponseDto);
        }
    }
    @Override
    public void notifyDoctor(Appointment appointment) {
        Doctor doctor = appointment.getDoctor();
        messagingTemplate.convertAndSendToUser(doctor.getUser().getUserName(),"/topic/appointments", appointment);
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
    public ResponseEntity<PrescriptionsAndRecords> getAppointmentDetails(String jwt,Long appointmentId) {
        String role = JwtProvider.getRoleFromJwtToken(jwt);

        if (role.equals("doctor")) {
            List<String> records = recordsRepository.findRecordsImageByAppointmentId(appointmentId);
            String prescription = prescriptionRepository.findPrescriptionImageByAppointmentID(appointmentId);
            Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId);
            PrescriptionsAndRecords appointmentDetails = new PrescriptionsAndRecords(records, prescription,appointment);
            return ResponseEntity.ok(appointmentDetails);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


}
