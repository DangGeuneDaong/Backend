package com.dgd.model.entity;

import com.dgd.model.type.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String user;
    private String message;
    private LocalDateTime sendAt;
    @Enumerated(EnumType.STRING)
    private MessageType messageType;
}
