package com.furniro.MessageService.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furniro.MessageService.database.entity.Message;

public interface MessageRepository  extends JpaRepository<Message,Long>{
    
}
