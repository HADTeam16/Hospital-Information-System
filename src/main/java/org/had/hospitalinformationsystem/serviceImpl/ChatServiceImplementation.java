package org.had.hospitalinformationsystem.serviceImpl;

import org.had.hospitalinformationsystem.model.Chat;
import org.had.hospitalinformationsystem.model.Doctor;
import org.had.hospitalinformationsystem.model.User;
import org.had.hospitalinformationsystem.repository.ChatRepository;
import org.had.hospitalinformationsystem.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImplementation implements ChatService {
    @Autowired
    ChatRepository chatRepository;


    @Override
    public Chat createChat(User reqUser, User user2) {
        Chat isExist=chatRepository.findChatByUsersId(user2,reqUser);
        if(isExist!=null){
            return isExist;
        }
        Chat chat=new Chat();
        chat.getUsers().add(user2);
        chat.getUsers().add(reqUser);
        chat.setTimeStamp(LocalDateTime.now());
        return chatRepository.save(chat);
    }

    @Override
    public Chat findChatById(Long chatId) throws Exception {
        Optional<Chat> opt=chatRepository.findById(chatId);
        if(opt.isEmpty()){
            throw new Exception("chat not found with id - "+ chatId);
        }

        return opt.get();
    }

    @Override
    public List<Chat> findUsersChat(Long userId) {
        return chatRepository.findByUsersId(userId);
    }
}
