package org.had.hospitalinformationsystem.user;

import org.had.hospitalinformationsystem.OT.OTRepository;
import org.had.hospitalinformationsystem.appointment.AppointmentRepository;
import org.had.hospitalinformationsystem.doctor.Doctor;
import org.had.hospitalinformationsystem.doctor.DoctorRepository;
import org.had.hospitalinformationsystem.doctor.DoctorService;
import org.had.hospitalinformationsystem.dto.HospitalLiveStatsDto;
import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.nurse.Nurse;
import org.had.hospitalinformationsystem.nurse.NurseRepository;
import org.had.hospitalinformationsystem.patient.Patient;
import org.had.hospitalinformationsystem.patient.PatientRepository;
import org.had.hospitalinformationsystem.receptionist.Receptionist;
import org.had.hospitalinformationsystem.receptionist.ReceptionistRepository;
import org.had.hospitalinformationsystem.ward.WardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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


    @Override
    public User findUserById(Long userId) throws Exception {
        Optional<User> user= userRepository.findById(userId);
        if(user.isPresent()){
            return user.get();
        }
        throw new Exception("user does not exist with userid " + userId);
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
        statsDto.setTotalWards(24);
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
}
