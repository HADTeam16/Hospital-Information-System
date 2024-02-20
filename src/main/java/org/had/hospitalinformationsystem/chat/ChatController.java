package org.had.hospitalinformationsystem.chat;

import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
    @Autowired
    ChatService chatService;
    @Autowired
    UserService userService;

    @GetMapping("/createChat/{doctorId}")
    public ResponseEntity<?> createChatRequest(@RequestHeader("Authorization") String jwt, @PathVariable Long doctorId) {
        try {
            User reqUser = userService.findUserByJwt(jwt);
            User user2 = userService.findUserById(doctorId);
            if (!reqUser.getRole().equals("doctor") || !user2.getRole().equals("doctor")) {
                throw new Exception("Only doctor to doctor chat is permitted.");
            }
            Chat chat = chatService.createChat(reqUser, user2);
            return ResponseEntity.ok(chat);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create chat: " + e.getMessage());
        }
    }

    @GetMapping("/userChats")
    public ResponseEntity<?> findUsersChatRequest(@RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserByJwt(jwt);
            List<Chat> chats = chatService.findUsersChat(user.getId());
            return ResponseEntity.ok(chats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve chats: " + e.getMessage());
        }
    }
}
