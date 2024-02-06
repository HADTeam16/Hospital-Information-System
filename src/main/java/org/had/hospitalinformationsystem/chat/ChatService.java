package org.had.hospitalinformationsystem.chat;

import org.had.hospitalinformationsystem.chat.Chat;
import org.had.hospitalinformationsystem.user.User;

import java.util.List;

public interface ChatService {

    public Chat createChat(User reqUser, User user2);

    public Chat findChatById(Long chatId) throws Exception;

    public List<Chat> findUsersChat(Long userId);
}
