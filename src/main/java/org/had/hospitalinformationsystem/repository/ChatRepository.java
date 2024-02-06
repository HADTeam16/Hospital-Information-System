package org.had.hospitalinformationsystem.repository;

import org.had.hospitalinformationsystem.model.Chat;
import org.had.hospitalinformationsystem.model.Doctor;
import org.had.hospitalinformationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Long> {
    List<Chat> findByUsersId(Long userId);

    @Query("select c from Chat c Where :user Member of c.users And :reqUser Member of c.users")
    public Chat findChatByUsersId(@Param("user") User user, @Param("reqUser") User reqUser);
}
