package com.ll.finhabit.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationSettingResponse {
    private boolean enabled;
}
