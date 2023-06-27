package com.dgd.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EnterChatRoomDto {
    private Long roomId;
    private String userId;
}
