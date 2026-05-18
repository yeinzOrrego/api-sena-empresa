package com.yapps.senaempresa.service;

import com.yapps.senaempresa.model.dto.TransferNotificationDto;

public interface NotificationService {
    void notifyTransferRequest(TransferNotificationDto dto);

}
