package org.had.hospitalinformationsystem.ward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class WardService {
    @Autowired
    WardRepository wardRepository;
    public void createInitialWards(){
        for(int floor=1;floor<=3;floor++){
            for(int wardNumber=1;wardNumber<=8;wardNumber++){
                Ward ward=new Ward();
                ward.setFloor(floor);
                ward.setWardNumber(String.valueOf(((floor-1)*8)+wardNumber));
                ward.setAvailableStatus(true);
                ward.setVipDeluxeStatus(Ward.VipDeluxeStatus.values()[new Random().nextInt(Ward.VipDeluxeStatus.values().length)]);
                wardRepository.save(ward);
            }
        }
    }
}
