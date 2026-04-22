package com.furniro.MessageService.dto.req;

import com.furniro.MessageService.util.MessageType;

import lombok.Data;

@Data
public class ConversationReq {
    private Integer buyerId;
    private Integer staffId;
    private String message;
    private MessageType messageType;
}
