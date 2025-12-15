package com.ll.finhabit.domain.notification.repository;

import com.ll.finhabit.domain.notification.entity.NotificationSetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByUserId(Long userId);
}
