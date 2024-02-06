package org.had.hospitalinformationsystem.serviceImpl;

import org.had.hospitalinformationsystem.model.Chat;
import org.had.hospitalinformationsystem.model.Message;
import org.had.hospitalinformationsystem.model.User;
import org.had.hospitalinformationsystem.repository.ChatRepository;
import org.had.hospitalinformationsystem.repository.MessagerRepository;
import org.had.hospitalinformationsystem.service.ChatService;
import org.had.hospitalinformationsystem.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImplementation implements MessageService {
    @Autowired
    private MessagerRepository messagerRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatService chatService;


    @Override
    public Message createMessage(User user, Long chatId, Message req) throws Exception {
        Message message=new Message();
        Chat chat=chatService.findChatById(chatId);

        message.setChat(chat);
        message.setContent(req.getContent());
        message.setUser(user);
        message.setTimeStamp(LocalDateTime.now());

        Message savedMessage=messagerRepository.save(message);
        chat.getMessages().add(savedMessage);
        chatRepository.save(chat);
        return savedMessage;

    }

    @Override
    public List<Message> findChatMessages(Long chatId) throws Exception {
        //Chat chat=chatService.findChatById(chatId);
        return messagerRepository.findByChatId(chatId);
    }
}
