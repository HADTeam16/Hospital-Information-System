package org.had.hospitalinformationsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<Doctor> doctors=new ArrayList<>();

    @OneToMany(mappedBy = "chat")
    private List<Message> messages=new ArrayList<>();
    private LocalDateTime timeStamp;

}