package com.dgd.service;

import com.dgd.exception.error.AuthenticationException;
import com.dgd.exception.error.ChatException;
import com.dgd.model.dto.CreateChatRoomDto;
import com.dgd.model.dto.EnterChatRoomDto;
import com.dgd.model.entity.ChatRoom;
import com.dgd.model.entity.Good;
import com.dgd.model.repo.ChatRoomRepository;
import com.dgd.model.repo.SharingApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.dgd.exception.message.AuthErrorMessage.*;
import static com.dgd.exception.message.ChatErrorMessage.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final SharingApplicationRepository sharingApplicationRepository;

    public ChatRoom createChatRoom(CreateChatRoomDto createChatRoomDto) {
        Good good = sharingApplicationRepository
                .findGoodById(createChatRoomDto.getSharingApplicationId())
                .orElseThrow(() -> new ChatException(NOT_ACTIVATED_APPLICATION)).getGood();

        String offerId = good.getUser().getUserId();

        ChatRoom chatRoom = ChatRoom.builder()
                .offerId(offerId)
                .takerId(createChatRoomDto.getUserId())
                .build();

        return chatRoomRepository.save(chatRoom);
    }
    public String enterChatRoom(EnterChatRoomDto enterChatRoomDto) throws ChatException {
        ChatRoom chatRoom = chatRoomRepository.findById(enterChatRoomDto.getRoomId())
                .orElseThrow(() -> new ChatException(NOT_ACTIVATED_APPLICATION));
        if(!enterChatRoomDto.getUserId().equals(chatRoom.getOfferId())) {
            throw new AuthenticationException(UNAUTHORIZED_REQUEST);
        }
        if (!enterChatRoomDto.getUserId().equals(chatRoom.getTakerId())) {
            throw new AuthenticationException(UNAUTHORIZED_REQUEST);
        }
        return enterChatRoomDto.getRoomId().toString();
    }

    public void deleteRoom(Long roomId) {
        chatRoomRepository.deleteById(roomId);
    }
}
