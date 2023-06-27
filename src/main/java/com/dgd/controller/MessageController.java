package com.dgd.controller;

import com.dgd.model.entity.Message;
import com.dgd.model.type.MessageType;
import com.dgd.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomService chatRoomService;


    @MessageMapping("/message/{roomId}")
    public void message(@DestinationVariable(value = "roomId")Long roomId, Message message) {
        if (MessageType.ENTER.equals(message.getMessageType())) {
            message.setMessage(message.getUser() + "님이 입장하셨습니다.");
        }
        if (MessageType.LEAVE.equals(message.getMessageType())) {
            message.setMessage(message.getUser() + "님이 퇴장하셨습니다.");
            chatRoomService.deleteRoom(roomId);
        }
        message.setSendAt(LocalDateTime.now());
        messagingTemplate.convertAndSend("/sub/" + roomId, message.getMessage());
    }
}
