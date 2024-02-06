package org.had.hospitalinformationsystem.message;

import org.had.hospitalinformationsystem.chat.Chat;
import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.chat.ChatRepository;
import org.had.hospitalinformationsystem.chat.ChatService;
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
