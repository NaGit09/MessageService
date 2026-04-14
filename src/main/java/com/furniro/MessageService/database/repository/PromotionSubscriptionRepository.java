package com.furniro.MessageService.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furniro.MessageService.database.entity.PromotionSubscription;

public interface PromotionSubscriptionRepository extends JpaRepository<PromotionSubscription, Long> {
    boolean existsByEmail(String email);
}