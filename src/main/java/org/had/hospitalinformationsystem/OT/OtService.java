package org.had.hospitalinformationsystem.OT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtService {
    @Autowired
    OTRepository otRepository;

    public void createOts(){
        for(int i=1;i<=10;i++){
            OT ot=new OT();
            ot.setAvailableStatus(true);
            otRepository.save(ot);
        }
    }

}
