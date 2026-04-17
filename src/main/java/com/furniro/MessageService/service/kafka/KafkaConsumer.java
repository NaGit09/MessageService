package com.furniro.MessageService.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.furniro.MessageService.service.MailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final MailService mailService;

    @Transactional
    @KafkaListener(topics = "email.auth.active", groupId = "auth-service-group")
    public void listen(Map<String, Object> event) {
        String email = (String) event.get("email");
        String username = (String) event.get("username");
        String token = (String) event.get("token");
        mailService.sendMailActive(email, username, token);
    }
}