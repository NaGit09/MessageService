package com.furniro.MessageService.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.furniro.MessageService.database.entity.Promotion;
import com.furniro.MessageService.database.entity.Subscription;
import com.furniro.MessageService.database.repository.PromotionRepository;
import com.furniro.MessageService.database.repository.SubscriptionRepository;
import com.furniro.MessageService.dto.API.AType;
import com.furniro.MessageService.dto.API.ApiType;
import com.furniro.MessageService.dto.req.PromotionReq;
import com.furniro.MessageService.exception.PromotionException;
import com.furniro.MessageService.util.PromotionErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final MailService mailService;

    public AType createPromotion(PromotionReq req) {
        // 1. create promotion
        Promotion promotion = Promotion.builder()
                .code(req.getCode())
                .title(req.getTitle())
                .description(req.getDescription())
                .type(req.getType())
                .value(req.getValue())
                .status(req.getStatus())
                .build();

        // 2. save promotion
        promotionRepository.save(promotion);
        List<Subscription> subscribers = subscriptionRepository.findAll();

        // 3. Gửi bất đồng bộ (giả sử mailService đã được đánh dấu @Async)
        subscribers.forEach(subscriber -> {
            mailService.sendMailPromotion(
                    subscriber.getEmail(),
                    subscriber.getFullName(),
                    promotion.getTitle(),
                    promotion.getDescription(),
                    promotion.getCode());
        });

        log.info("Promotion created and emails are being sent in background: {}", promotion.getCode());

        return ApiType.builder()
                .code(200)
                .message("Promotion created successfully")
                .data(promotion)
                .build();
    }

    public AType deletePromotion(Long id) {

        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionException(PromotionErrorCode.PROMOTION_NOT_FOUND));

        promotionRepository.delete(promotion);
        
        return ApiType.builder()
                .code(200)
                .message("Promotion deleted successfully")
                .build();
    }

    public AType getAllPromotion(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Promotion> promotions = promotionRepository.findAll(pageable);
        return ApiType.builder()
                .code(200)
                .message("Get all promotions successfully")
                .data(promotions)
                .build();
    }

}
