package com.furniro.MessageService.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furniro.MessageService.dto.req.Mail.MailActiveReq;
import com.furniro.MessageService.dto.req.Mail.MailOTPReq;
import com.furniro.MessageService.dto.req.Notify.NotificationReq;
import com.furniro.MessageService.service.Notification.NotificationService;
import com.furniro.MessageService.service.Other.MailService;
import com.furniro.MessageService.service.Conversation.AiChatService;
import com.furniro.MessageService.service.Conversation.MessageService;
import com.furniro.MessageService.dto.req.Message.MessageReq;
import com.furniro.MessageService.util.enums.MessageType;
import com.furniro.MessageService.database.entity.Message;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final MailService mailService;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final AiChatService aiChatService;
    private final MessageService messageService;

    @Transactional
    @KafkaListener(
            topics = "auth.send.active", 
            groupId = "message-service-group", 
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onUserCreated(Map<String, Object> event) {
        try {

            log.info("Received auth.send.active event: {}", event);

            MailActiveReq req = objectMapper.convertValue(event, MailActiveReq.class);
            
            String fullName = req.getFirstName() + " " + req.getLastName();
            
            mailService.sendMailActive(req.getEmail(), fullName, req.getAccountID());

        } catch (Exception e) {
            log.error("Failed to process auth.send.active event: {}", e.getMessage());
        }

    }

    @Transactional
    @KafkaListener(
        topics = "auth.send.otp", 
        groupId = "message-service-group", 
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMailOTP(Map<String, Object> event) {
        try {

            log.info("Received auth.send.otp event: {}", event);

            MailOTPReq req = objectMapper.convertValue(event, MailOTPReq.class);
            mailService.sendMailOTP(req.getEmail(), req.getUserName(), req.getOtp());

        } catch (Exception e) {
            log.error("Failed to process auth.send.otp event: {}", e.getMessage());
        }

    }

    @Transactional
    @KafkaListener(
        topics = "notification.created", 
        groupId = "message-service-group", 
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onNotificationCreated(Map<String, Object> message) {

        try {

            log.info("Received notification.created event: {}", message);

            NotificationReq req = objectMapper.convertValue(message, NotificationReq.class);

            notificationService.createNotification(req);
            
            messagingTemplate.convertAndSend("/topic/notifications/" + req.getUserID(), req);

        } catch (Exception e) {

            log.error("Failed to process notification.created event: {}", e.getMessage());
        }
    }

    @Transactional
    @KafkaListener(
        topics = "message.created",
        groupId = "message-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessageCreated(Map<String, Object> event) {
        try {
            log.info("Received message.created event from Kafka: {}", event);

            Integer conversationId = (Integer) event.get("conversationId");
            String content = (String) event.get("content");
            Integer senderId = (Integer) event.get("senderId");
            Integer receiverId = (Integer) event.get("receiverId");

            // Avoid infinite loops (only respond if sent to bot)
            if (Integer.valueOf(1).equals(receiverId) && !Integer.valueOf(1).equals(senderId)) {
                // Call Gemini to get completion response
                String aiResponse = aiChatService.getChatCompletion(content, conversationId);

                // Check and process human handover
                boolean isHandover = aiChatService.checkAndProcessHandover(aiResponse, conversationId);

                String cleanResponse = aiResponse;
                if (isHandover) {
                    // Remove handover tag for cleaner display
                    cleanResponse = cleanResponse.replace("[HANDOVER]", "").trim();
                    if (cleanResponse.isEmpty()) {
                        cleanResponse = "I am transferring you to a live support agent right now. Please wait a moment.";
                    }
                }

                // Create message payload
                MessageReq botReplyReq = MessageReq.builder()
                        .conversationId(conversationId)
                        .senderId(1) // Bot ID
                        .receiverId(senderId) // Send back to customer
                        .content(cleanResponse)
                        .messageType(MessageType.TEXT)
                        .build();

                // Save bot reply
                Message savedBotMsg = messageService.createMessage(botReplyReq);

                // Broadcast bot reply to WebSocket topic
                String topic = "/topic/conversation/" + conversationId;
                messagingTemplate.convertAndSend(topic, savedBotMsg);
                log.info("AI Bot reply sent and broadcasted to topic: {}", topic);
            }
        } catch (Exception e) {
            log.error("Failed to process message.created event: {}", e.getMessage(), e);
        }
    }

}