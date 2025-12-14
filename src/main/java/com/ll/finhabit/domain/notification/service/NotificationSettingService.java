package com.ll.finhabit.domain.notification.service;

import com.ll.finhabit.domain.notification.dto.NotificationSettingResponse;
import com.ll.finhabit.domain.notification.entity.NotificationSetting;
import com.ll.finhabit.domain.notification.repository.NotificationSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationSettingService {

    private final NotificationSettingRepository notificationSettingRepository;

    public NotificationSettingResponse toggle(Long userId) {
        NotificationSetting setting = notificationSettingRepository.findByUserId(userId)
                .orElseGet(() -> notificationSettingRepository.save(
                        NotificationSetting.builder()
                                .userId(userId)
                                .enabled(true) // 기본값 ON
                                .build()
                ));

        setting.setEnabled(!setting.isEnabled());

        return NotificationSettingResponse.builder()
                .enabled(setting.isEnabled())
                .build();
    }
}
