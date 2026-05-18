package com.yapps.senaempresa.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.yapps.senaempresa.model.dto.TransferNotificationDto;
import com.yapps.senaempresa.service.NotificationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyTransferRequest(TransferNotificationDto dto) {
        log.info("Emitting transfer request notification via WebSocket with ID: {}", dto.getMovementId());

        messagingTemplate.convertAndSend("/topic/plantation-transfers", dto);
    }
}