package org.had.hospitalinformationsystem.message;

import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @PostMapping("/chat/{chatId}")
    public ResponseEntity<?> createMessage(@RequestBody Message req,
                                           @RequestHeader("Authorization") String jwt,
                                           @PathVariable Long chatId) {
        try {
            User user = userService.findUserByJwt(jwt);
            Message message = messageService.createMessage(user, chatId, req);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create message: " + e.getMessage());
        }
    }
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<?> findChatMessage(@RequestHeader("Authorization") String jwt,
                                             @PathVariable Long chatId) {
        try {
            User user = userService.findUserByJwt(jwt);
            List<Message> messages = messageService.findChatMessages(chatId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve messages: " + e.getMessage());
        }
    }
}
