package com.ll.finhabit.domain.notification.dto;

import com.ll.finhabit.domain.notification.entity.NotificationType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {
    private NotificationType type;
    private String title;
    private String message;
    private LocalDateTime createdAt;
}
