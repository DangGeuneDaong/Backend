package com.dgd.sevice;

import com.dgd.domain.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {
    private final RedisTemplate<String, ChatMessage> redisTemplate;
    private static final String CHAT_HISTORY_KEY = "chat_history";

    public void saveChatMessage(ChatMessage message) {
        redisTemplate.opsForList().leftPush(CHAT_HISTORY_KEY, message);
    }

    public List<ChatMessage> getChatHistory() {
        Long size = redisTemplate.opsForList().size(CHAT_HISTORY_KEY);
        if (size != null) {
            return redisTemplate.opsForList().range(CHAT_HISTORY_KEY, 0, size - 1);
        }
        return Collections.emptyList();
    }
}
