package com.furniro.MessageService.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.furniro.MessageService.database.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
}
