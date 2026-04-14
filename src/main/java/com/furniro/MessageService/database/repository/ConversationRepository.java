package com.furniro.MessageService.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furniro.MessageService.database.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

}
