package com.ll.finhabit.domain.notification.dto;

import com.ll.finhabit.domain.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    private NotificationType type;
    private String title;
    private String message;
    private LocalDateTime createdAt;
}
