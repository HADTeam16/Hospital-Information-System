package org.had.hospitalinformationsystem.chat;

import org.had.hospitalinformationsystem.user.User;
import org.had.hospitalinformationsystem.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    Chat createChatRequest(@RequestHeader("Authorization") String jwt, @PathVariable Long doctorId) throws Exception {
        User reqUser=userService.findUserByJwt(jwt);
        User user2=userService.findUserById(doctorId);
        Chat chat=chatService.createChat(reqUser,user2);
        return chat;
//        if(reqUser.getRole().equals(user2.getRole()) && reqUser.equals("role")){
//
//        }
//        else{
//            throw new Exception("Only doctor to doctor is possible right now");
//        }
    }

    @GetMapping
    public List<Chat> findUsersChatRequest(@RequestHeader("Authorization") String jwt){
        User user=userService.findUserByJwt(jwt);
        List<Chat> chats=chatService.findUsersChat(user.getId());

        return chats;
    }
}
