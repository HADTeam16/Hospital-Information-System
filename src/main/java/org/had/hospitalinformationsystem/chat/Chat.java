package org.had.hospitalinformationsystem.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.had.hospitalinformationsystem.message.Message;
import org.had.hospitalinformationsystem.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String chat_name;

    private String chat_image;
    @ManyToMany
    private List<User> users=new ArrayList<>();

    @OneToMany(mappedBy = "chat")
    private List<Message> messages=new ArrayList<>();
    private LocalDateTime timeStamp;

}