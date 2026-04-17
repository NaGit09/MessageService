package com.furniro.MessageService.service;

import org.springframework.stereotype.Service;

import com.furniro.MessageService.database.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    
}
