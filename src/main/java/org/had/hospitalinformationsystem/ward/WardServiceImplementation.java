package org.had.hospitalinformationsystem.ward;

import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.nurse.Nurse;
import org.had.hospitalinformationsystem.nurse.NurseRepository;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WardServiceImplementation implements WardService{

    @Autowired
    WardRepository wardRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    NurseRepository nurseRepository;

    @Override
    public List<Ward> bookedWard(String jwt){
        return wardRepository.findBookedWard();
    }

    @Override
    public void createInitialWards(){
        for(int floor=1;floor<=3;floor++){
            for(int wardNumber=1;wardNumber<=10;wardNumber++){
                Ward ward=new Ward();
                ward.setFloor(floor);
                ward.setWardNumber(String.valueOf(((floor-1)*8)+wardNumber));
                ward.setAvailableStatus(true);
                ward.setEmergency(false);
                wardRepository.save(ward);
            }
        }
    }

    @Override
    public ResponseEntity<Map<String,String>> callEmergency(String jwt, Long wardId){
        String role= JwtProvider.getRoleFromJwtToken(jwt);
        Map<String,String> response=new HashMap<>();
        if(!role.equals("nurse")){
            response.put("message","only assigned nurse can call emergency");
            return ResponseEntity.badRequest().body(response);
        }
        String userName=JwtProvider.getUserNameFromJwtToken(jwt);
        User user=userRepository.findByUserName(userName);
        Optional<Nurse> nurse=nurseRepository.findById(user.getId());
        Optional<Ward> ward=wardRepository.findById(wardId);
        if(nurse.isPresent() & ward.isPresent()){

            ward.get().setEmergency(!ward.get().isEmergency());

            response.put("message","Ward Emergency Status has been updated");
            wardRepository.save(ward.get());
            return ResponseEntity.ok().body(response);


        }
        response.put("message","Either assi nurse not found or ward not found");
        return ResponseEntity.badRequest().body(response);
    }

}
