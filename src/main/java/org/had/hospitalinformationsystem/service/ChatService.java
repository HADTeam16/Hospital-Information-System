package org.had.hospitalinformationsystem.service;

import org.had.hospitalinformationsystem.model.Chat;
import org.had.hospitalinformationsystem.model.Doctor;
import org.had.hospitalinformationsystem.model.User;

import java.util.List;

public interface ChatService {

    public Chat createChat(User reqUser, User user2);

    public Chat findChatById(Long chatId) throws Exception;

    public List<Chat> findUsersChat(Long userId);
}
