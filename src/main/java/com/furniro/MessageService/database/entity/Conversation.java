package com.furniro.MessageService.database.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

import com.furniro.MessageService.util.enums.MessageType;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private Integer buyerId;

    @NotNull
    @Builder.Default
    private Integer staffId = 1;

    @Builder.Default
    private LocalDateTime lastMessageAt = LocalDateTime.now();

    
    @Builder.Default
    private String lastMessageContent = "";

    @Builder.Default
    private MessageType lastMessageType = MessageType.TEXT;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}