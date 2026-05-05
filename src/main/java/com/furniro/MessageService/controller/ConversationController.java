package com.furniro.MessageService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.furniro.MessageService.dto.API.AType;
import com.furniro.MessageService.dto.req.Message.ConversationReq;

import jakarta.validation.Valid;
import com.furniro.MessageService.service.Conversation.ConversationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @PostMapping("/create")
    public ResponseEntity<AType> createConversation(@Valid @RequestBody ConversationReq req) {
        return conversationService.createConversation(req);
    }

    @GetMapping("/all")
    public ResponseEntity<AType> getAllConversation(@RequestParam Integer userId) {
        return conversationService.getAllConversation(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AType> getConversationById(@PathVariable int id) {
        return conversationService.getConversationById(id);
    }
}
