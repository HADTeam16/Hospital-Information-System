package org.had.hospitalinformationsystem.service;

import org.had.hospitalinformationsystem.model.Doctor;


import java.util.List;

public interface DoctorService {



    Doctor findDoctorById(Long doctorId) throws Exception;


    Doctor updateDoctor(Doctor doctor, Long doctorId);

    List<Doctor> searchDoctor(String  query);
}
