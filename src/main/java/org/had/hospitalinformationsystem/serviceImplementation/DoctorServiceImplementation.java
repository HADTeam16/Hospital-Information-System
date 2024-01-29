package org.had.hospitalinformationsystem.serviceImplementation;

import org.had.hospitalinformationsystem.config.JwtProvider;
import org.had.hospitalinformationsystem.model.Doctor;
import org.had.hospitalinformationsystem.repository.DoctorRepository;
import org.had.hospitalinformationsystem.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImplementation implements DoctorService {

    @Autowired
    DoctorRepository doctorRepository;
    @Override
    public List<Doctor> findDoctorBySpecialization(String specialization) throws Exception {
        return null;
    }

    @Override
    public Doctor findDoctorById(Long doctorId) throws Exception {
        Optional<Doctor> doctor= doctorRepository.findById(doctorId);

        if(doctor.isPresent()){
            return doctor.get();
        }
        throw new Exception("doctor does not exist with doctorid " + doctorId);
    }

    @Override
    public List<Doctor> searchDoctor(String query) {
        return doctorRepository.searchDoctor(query);
    }

    @Override
    public Doctor findDoctorByJwt(String jwt) {
        String userName= JwtProvider.getUserNameFromJwtToken(jwt);
        return doctorRepository.findByUserName(userName);
    }

    @Override
    public Doctor updateDoctor(Doctor doctor, Long doctorId) {
        Doctor oldDoctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist with id " + doctorId));
        if (doctor.getFirstName() != null) {
            oldDoctor.setFirstName(doctor.getFirstName());
        }
        if (doctor.getLastName() != null) {
            oldDoctor.setLastName(doctor.getLastName());
        }

        return doctorRepository.save(oldDoctor);
    }
}
