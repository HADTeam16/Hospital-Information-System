package org.had.hospitalinformationsystem.wardHistory;

import org.had.hospitalinformationsystem.jwt.JwtProvider;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserRepository;
import org.had.hospitalinformationsystem.ward.Ward;
import org.had.hospitalinformationsystem.ward.WardRepository;
import org.had.hospitalinformationsystem.ward.WardService;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WardHistoryServiceImpl implements WardHistoryService {

    @Autowired
    WardRepository wardRepository;
    @Autowired
    WardHistoryRepository wardHistoryRepository;
    @Autowired
    UserRepository userRepository;
    @Qualifier("jasyptStringEncryptor")
    @Autowired
    private StringEncryptor stringEncryptor;
    @Override
    public ResponseEntity<?> getWardHistory(String jwt,Long wardId) {
        String userName= JwtProvider.getUserNameFromJwtToken(jwt);
        User user=userRepository.findByUserName(userName);
        String role=JwtProvider.getRoleFromJwtToken(jwt);
        Map<String,String> response=new HashMap<>();
        Optional<Ward> ward=wardRepository.findById(wardId);
        if(ward.isPresent()){
            if(role.equals("doctor") || role.equals("nurse")){
                List<WardHistory> wardHistories=wardHistoryRepository.getWardHistoriesByAppointment(ward.get().getAppointment().getAppointmentId());
                for(WardHistory wardhist :wardHistories ){
                    wardhist.setAppointment(null);
                    wardhist.setBloodPressure(stringEncryptor.decrypt(wardhist.getBloodPressure()));
                }
                return ResponseEntity.ok().body(wardHistories);
            }
            response.put("message","Only doctor and nurse can see ward history");
            return ResponseEntity.badRequest().body(response);

        }
        response.put("message","Ward not found");
        return ResponseEntity.badRequest().body(response);
    }
}
