package org.had.hospitalinformationsystem.detailServiceImplementation;

import org.had.hospitalinformationsystem.model.Doctor;
import org.had.hospitalinformationsystem.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorDetailsServiceImplementation implements UserDetailsService {
    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Doctor doctor=doctorRepository.findByUserName(userName);
        if(doctor==null){
            throw new UsernameNotFoundException("doctor not found with email "+ userName);
        }
        List<GrantedAuthority> authorities=new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(doctor.getUserName(),doctor.getPassword(),authorities);
    }

}