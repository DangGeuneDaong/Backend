package com.dgd.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChatRoomDto {
    private String userId;
    private Long sharingApplicationId;
}
