package org.had.hospitalinformationsystem.message;

import org.had.hospitalinformationsystem.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessagerRepository extends JpaRepository<Message,Long> {

    public List<Message> findByChatId(Long chatId);
}
