package com.furniro.MessageService.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.furniro.MessageService.database.entity.Conversation;
import com.furniro.MessageService.database.entity.Message;
import com.furniro.MessageService.database.repository.ConversationRepository;
import com.furniro.MessageService.database.repository.MessageRepository;
import com.furniro.MessageService.dto.API.AType;
import com.furniro.MessageService.dto.API.ApiType;
import com.furniro.MessageService.dto.req.ConversationReq;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationService {
    final ConversationRepository conversationRepository;
    final MessageRepository messageRepository;

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
        messageRepository.save(message);

        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Create new conversation successfully !")
                .data(conversation)
                .build());
    }

    public ResponseEntity<AType> getAllConversation(Integer userId) {
        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Get all conversation successfully !")
                .data(conversationRepository.findByBuyerIdOrStaffId(userId, userId))
                .build());
    }

    public ResponseEntity<AType> getConversationById(int id) {
        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Get conversation by id successfully !")
                .data(conversationRepository.findById(id).orElse(null))
                .build());
    }
}
