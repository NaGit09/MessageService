package com.furniro.MessageService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.furniro.MessageService.dto.API.AType;
import com.furniro.MessageService.dto.req.SubscriptionReq;
import com.furniro.MessageService.service.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/subscribe")
@RequiredArgsConstructor
public class SubscriberController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/")
    public ResponseEntity<AType> getAllSubscriber(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String sortBy) {
        return subscriptionService.getAllSubscribers(page, size, sortBy);
    }

    @PostMapping("/")
    public ResponseEntity<AType> createSubscriber(
        @RequestBody SubscriptionReq req) {
        return subscriptionService.createSubscribe(req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AType> deleteSubscriber
        (@PathVariable Integer id) {
        return subscriptionService.deleteSubscriber(id);
    }
}
