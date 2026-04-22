package com.furniro.MessageService.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.furniro.MessageService.database.entity.Message;
import com.furniro.MessageService.dto.req.MessageReq;
import com.furniro.MessageService.service.MessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageReq messageReq) {
        // 1. create message
        Message message = messageService.createMessage(messageReq);

        // 2. create topic
        String topic = "/topic/conversation/"
                + messageReq.getConversationId();
                
        // 3. check message created
        if (message != null) {
            messagingTemplate.convertAndSend(topic, message);
            log.info("Message sent successfully");
        } else {
            log.error("Message not created");
        }
    }
}