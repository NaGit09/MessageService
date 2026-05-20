package com.furniro.MessageService.service.Conversation;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.furniro.MessageService.database.entity.Conversation;
import com.furniro.MessageService.database.entity.Message;
import com.furniro.MessageService.database.repository.ConversationRepository;
import com.furniro.MessageService.database.repository.MessageRepository;
import com.furniro.MessageService.dto.API.AType;
import com.furniro.MessageService.dto.API.ApiType;
import com.furniro.MessageService.dto.req.Message.ConversationReq;
import com.furniro.MessageService.exception.imp.MessageException;
import com.furniro.MessageService.service.kafka.KafkaProducer;
import com.furniro.MessageService.util.enums.MessageType;
import com.furniro.MessageService.util.error.MessageErrorCode;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationService {
        
    final ConversationRepository conversationRepository;
    final MessageRepository messageRepository;
    final KafkaProducer kafkaProducer;

    @Transactional
    public ResponseEntity<AType> createConversation(ConversationReq req) {
        // 1. create conversation
        Conversation conversation = Conversation.builder()
                .buyerId(req.getBuyerId())
                .staffId(req.getStaffId())
                .lastMessageContent(req.getMessage())
                .build();

        conversationRepository.save(conversation);

        // 2. create message
        Message message = Message.builder()
                .conversation(conversation)
                .senderId(req.getBuyerId())
                .receiverId(req.getStaffId() != null ? req.getStaffId() : 1)
                .content(req.getMessage())
                .type(req.getMessageType())
                .build();

        // 3 if message type is Image , send kafka active image
        if (MessageType.IMAGE.equals(req.getMessageType())) {
            // send kafka active image
            Map<String, Object> payload = Map.of(
                    "fileID", req.getFileId()
            );
            kafkaProducer.send("upload.active", payload);

        }
        
        messageRepository.save(message);

        return ResponseEntity.ok(ApiType.success(conversation));
    }

    public ResponseEntity<AType> getAllConversation(Integer userId) {
        
        return ResponseEntity.ok(ApiType.success(conversationRepository.findByBuyerIdOrStaffId(userId, userId)));
    }

    public ResponseEntity<AType> getConversationById(int id) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

        return ResponseEntity.ok(ApiType.success(conversation));
    }
}
