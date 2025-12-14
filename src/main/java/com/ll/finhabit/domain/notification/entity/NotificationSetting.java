package com.ll.finhabit.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "notification_setting",
        uniqueConstraints = @UniqueConstraint(name = "uk_notification_setting_user", columnNames = "user_id")
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    // ✅ 전체 알림 ON/OFF
    @Column(nullable = false)
    private boolean enabled;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
