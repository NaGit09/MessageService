package com.furniro.MessageService.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furniro.MessageService.database.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    List<Conversation> findByBuyerId(Integer buyerId);
    List<Conversation> findByStaffId(Integer staffId);
    List<Conversation> findByBuyerIdOrStaffId(Integer buyerId, Integer staffId);
}
