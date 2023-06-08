package com.dgd.domain.model;


import com.dgd.domain.type.MessageType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessage {
    private String roomId;
    private String userId;
    private String message;
    private LocalDateTime sentAt;
    private MessageType type;
}
