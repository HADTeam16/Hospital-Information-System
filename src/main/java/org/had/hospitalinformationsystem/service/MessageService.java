package org.had.hospitalinformationsystem.service;

import org.had.hospitalinformationsystem.model.Message;
import org.had.hospitalinformationsystem.model.User;

import java.util.List;

public interface MessageService {

    Message createMessage(User user,Long chatId,Message req) throws Exception;

    List<Message> findChatMessages(Long chatId) throws Exception;
}
