package com.furniro.MessageService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.furniro.MessageService.dto.API.AType;
import com.furniro.MessageService.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/")
    public ResponseEntity<AType> getNotifications(
        @RequestParam Integer receiverID,
        @RequestParam Integer page,
        @RequestParam Integer size,
        @RequestParam String sortBy) {
        return notificationService.getAllNotifications(receiverID, page, size, sortBy);
    }

    @PatchMapping("/{notificationID}/read")
    public ResponseEntity<AType> readNotification(@PathVariable Integer notificationID) {
        return notificationService.readNotification(notificationID);
    }

}
