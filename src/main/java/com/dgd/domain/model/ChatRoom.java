package com.dgd.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {
    @Id
    private String roomId;
    private String offerId;
    private String takerId;
    private Long goodId;


    public static ChatRoom create(String offerId) {
        ChatRoom room = new ChatRoom();
        room.roomId = UUID.randomUUID().toString();
        room.offerId = offerId;
        return room;
    }
}