package org.had.hospitalinformationsystem.message;

import org.had.hospitalinformationsystem.message.Message;
import org.had.hospitalinformationsystem.user.User;

import java.util.List;

public interface MessageService {

    Message createMessage(User user,Long chatId,Message req) throws Exception;

    List<Message> findChatMessages(Long chatId) throws Exception;
}
