package com.furniro.MessageService.dto.req;

import com.furniro.MessageService.util.MessageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageReq {
    private Integer conversationId;
    private String content;
    private Integer receiverId;
    private Integer senderId;
    private MessageType messageType;
}
