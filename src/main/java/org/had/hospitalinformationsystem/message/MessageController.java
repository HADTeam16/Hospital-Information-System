package org.had.hospitalinformationsystem.message;

import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    Message createMessage(@RequestBody Message req,
                          @RequestHeader("Authorization") String jwt,
                          @PathVariable Long chatId) throws Exception {
        User user=userService.findUserByJwt(jwt);
        Message message=messageService.createMessage(user,chatId,req);
        return message;

    }
    @GetMapping("/chat/{chatId}")
    List<Message> findChatMessage(@RequestHeader("Authorization") String jwt,
                                  @PathVariable Long chatId) throws Exception {
        User user=userService.findUserByJwt(jwt);
        List<Message> messages=messageService.findChatMessages(chatId);
        return messages;
    }
}
