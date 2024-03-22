package org.had.hospitalinformationsystem.needWard;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NeedWardServiceImpl implements NeedWardService{
    @Autowired
    NeedWardRepository needWardRepository;
    @Override
    public void deleteNeedWardById(Long needWardId) {
        if (needWardRepository.existsById(needWardId)) {
            needWardRepository.deleteById(needWardId);
        } else {
            throw new EntityNotFoundException("NeedWard with ID " + needWardId + " not found.");
        }
    }
}
