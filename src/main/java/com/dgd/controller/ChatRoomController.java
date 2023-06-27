package com.dgd.controller;

import com.dgd.model.dto.EnterChatRoomDto;
import com.dgd.model.entity.ChatRoom;
import com.dgd.service.ChatRoomService;
import com.dgd.model.dto.CreateChatRoomDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/enter")
    public String enterChatRoom(@RequestBody @Valid EnterChatRoomDto enterChatRoomDto) {
        return chatRoomService.enterChatRoom(enterChatRoomDto);
    }

    @DeleteMapping("/leave")
    public void leaveChatRoom(@RequestParam @Valid Long roomId) {
        chatRoomService.deleteRoom(roomId);
    }
}
