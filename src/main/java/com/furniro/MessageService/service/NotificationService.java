package com.furniro.MessageService.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.furniro.MessageService.database.entity.Notification;
import com.furniro.MessageService.database.repository.NotificationRepository;
import com.furniro.MessageService.dto.API.AType;
import com.furniro.MessageService.dto.API.ApiType;
import com.furniro.MessageService.dto.req.NotificationReq;
import com.furniro.MessageService.exception.NotifyException;
import com.furniro.MessageService.util.NotificationErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public ResponseEntity<AType> getAllNotifications
        (Integer receiverID, Integer page, Integer size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Page<Notification> notifications = notificationRepository.findByUserID(receiverID, pageable);

        if (notifications == null) {
            throw new NotifyException(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
        }

        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("get all notification successfully")
                .data(notifications)
                .build());
    }

    public ResponseEntity<AType> createNotification
        (NotificationReq req) {
        // create notify
        Notification notification = Notification.builder()
                .userID(req.getUserID())
                .title(req.getTitle())
                .content(req.getMessage())
                .type(req.getType())
                .build();
        // save notify
        notificationRepository.save(notification);

        // return response
        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("create notification successfully")
                .data(notification)
                .build());
    }

    public ResponseEntity<AType> readNotification
        (Integer id) {
        // find notify
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() ->
                        new NotifyException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
        // set read true
        notification.setIsRead(true);
        // save notify
        notificationRepository.save(notification);
        // return response
        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("read notification successfully")
                .data(notification)
                .build());
    }

}
