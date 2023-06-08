package com.dgd.kafka;

import com.dgd.domain.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(value = "/kafka")
@RequiredArgsConstructor
public class KafkaProducerController {

    private final KafkaTemplate<String, MessageDto> kafkaTemplate;

    //producer 부분
    @PostMapping("/send")
    public void sendMessage(@RequestBody @Valid MessageDto message) {
        message.setSendAt(LocalDateTime.now().toString());
        log.info("Produce message : " + message.toString());
        try {
            kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC, message).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //여기서 프론트엔드로 메시지를 전송합니다.
    @MessageMapping("/sendMessage")
    @SendTo("/topic/group")
    public MessageDto broadcastGroupMessage(@Payload MessageDto message) {
        return message;
    }
}