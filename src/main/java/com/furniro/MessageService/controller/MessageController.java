package com.furniro.MessageService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.furniro.MessageService.dto.API.AType;
import com.furniro.MessageService.service.MessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/")
    public ResponseEntity<AType> getMessages(
            @RequestParam Integer conversationID,
            @RequestParam Integer page,
            @RequestParam Integer size) {
        return messageService.getAllMessage(conversationID, page, size);
    }

}
