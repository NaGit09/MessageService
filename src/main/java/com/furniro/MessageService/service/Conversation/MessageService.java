package com.furniro.MessageService.service.Conversation;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.furniro.MessageService.database.entity.Conversation;
import com.furniro.MessageService.database.entity.Message;
import com.furniro.MessageService.database.repository.ConversationRepository;
import com.furniro.MessageService.database.repository.MessageRepository;
import com.furniro.MessageService.dto.API.AType;
import com.furniro.MessageService.dto.API.ApiType;
import com.furniro.MessageService.dto.req.Message.MessageReq;
import com.furniro.MessageService.exception.imp.MessageException;
import com.furniro.MessageService.util.error.MessageErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    @Transactional
    public ResponseEntity<AType> isRead(Integer messageID) {
        // 1. find message
        Message message = messageRepository.findById(messageID)
                .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

        // 2. mark as read
        message.setIsRead(true);
        
        // 3. save message
        messageRepository.save(message);

        // 4. return response
        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Message read successfully")
                .data(message)
                .build());
    }

    public ResponseEntity<AType> getAllMessage(Integer conversationID, Integer page, Integer size) {
        // 1. find conversation
        Conversation conversation = conversationRepository.findById(conversationID)
                .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

        // 2. get all message by conversation
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findAllByConversation(conversation, pageable);

        // 3. return response
        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Messages retrieved successfully")
                .data(messages)
                .build());
    }

    @Transactional
    public Message createMessage(MessageReq messageReq) {
        // 1. find conversation
        Conversation conversation = conversationRepository.findById(messageReq.getConversationId())
                .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

        // 2. create message
        Message message = Message.builder()
                .conversation(conversation)
                .content(messageReq.getContent())
                .receiverId(messageReq.getReceiverId())
                .senderId(messageReq.getSenderId())
                .type(messageReq.getMessageType())
                .build();

        // 3. update conversation last message info
        LocalDateTime now = LocalDateTime.now();
        conversation.setLastMessageContent(message.getContent());
        conversation.setLastMessageAt(now);

        // 4. save message and conversation
        messageRepository.save(message);
        conversationRepository.save(conversation);

        // 5. return response
        return message;
    }
}
