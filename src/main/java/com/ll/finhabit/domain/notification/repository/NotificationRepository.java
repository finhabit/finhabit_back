package com.ll.finhabit.domain.notification.repository;

import com.ll.finhabit.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 알림센터용: 내 알림 전체 조회 (최신순)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
