package com.furniro.MessageService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.furniro.MessageService.dto.API.AType;
import com.furniro.MessageService.dto.req.PromotionReq;
import com.furniro.MessageService.service.PromotionService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/promotion")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionController {

    // 1. inject
    private final PromotionService promotionService;

    @PostMapping("/")
    public ResponseEntity<AType> createPromotion(
            @RequestBody PromotionReq req) {
        return promotionService.createPromotion(req);
    }

    @GetMapping("/all")
    public ResponseEntity<AType> getPromotion(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        return promotionService.getAllPromotion(page, size, sortBy);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AType> deletePromotion(
            @PathVariable Integer id) {
        return promotionService.deletePromotion(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AType> updatePromotion(
            @RequestBody PromotionReq req) {
        return promotionService.updatePromotion(req);
    }
}
