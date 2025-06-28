package com.maciu19.autobidder.api.notification.service;

import com.maciu19.autobidder.api.notification.model.Notification;
import com.maciu19.autobidder.api.notification.model.NotificationType;
import com.maciu19.autobidder.api.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createAndSaveNotification(UUID userId, NotificationType type, String payload) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .payload(payload)
                .build();

        return notificationRepository.save(notification);
    }

    @Transactional()
    public List<Notification> getUnreadNotificationsForUser(UUID userId) {
        return notificationRepository.findByUserIdAndReadAtIsNullOrderByCreatedDateDesc(userId);
    }

    @Transactional
    public void markNotificationAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        if (!notification.getUserId().equals(userId)) {
            throw new SecurityException("User does not have permission to update this notification.");
        }

        if (notification.getReadAt() == null) {
            notification.setReadAt(Instant.now());
            notificationRepository.save(notification);
        }
    }
}
