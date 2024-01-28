package org.had.hospitalinformationsystem.service;

import org.had.hospitalinformationsystem.model.Doctor;
import org.had.hospitalinformationsystem.model.User;

import java.util.List;


public interface DoctorService {


    List<Doctor> findDoctorBySpecialization(String specialization) throws Exception;


    Doctor findDoctorById(Long doctorId) throws Exception;




    List<Doctor> searchDoctor(String  query);
    Doctor findDoctorByJwt(String jwt);

    Doctor updateDoctor(Doctor doctor, Long doctorId);
}
