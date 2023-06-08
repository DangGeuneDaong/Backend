package com.dgd.controller;

import com.dgd.domain.model.ChatMessage;
import com.dgd.domain.type.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations sendingOperations;

    @MessageMapping("/come")
    public void enter(ChatMessage message) {
        if (MessageType.ENTER.equals(message.getType())) {
            message.setMessage(message.getUserId() + " 님이 입장하였습니다.");
        }
        sendingOperations.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);
    }
}

// N개 부터는 오브젝트 단위로 캐싱을하고 재고 수량은 들어오는 순서대로 하나씩받아서 처리할 수 있게끔 스택
// 웨이팅 리스트를 만들면 되지 않나?