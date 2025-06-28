package com.maciu19.autobidder.api.notification.service;

import com.maciu19.autobidder.api.notification.model.Notification;
import com.maciu19.autobidder.api.notification.model.NotificationType;
import com.maciu19.autobidder.api.notification.repository.NotificationRepository;
import com.maciu19.autobidder.api.user.model.User;
import com.maciu19.autobidder.api.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    public NotificationServiceImpl(NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    @Transactional
    public void createUserSpecificNotification(UUID userId, NotificationType type, String payload) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .payload(payload)
                .build();

        notificationRepository.save(notification);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId,
                notification
        );
    }

    @Transactional
    public void createGlobalNotification(String payload, NotificationType type) {
        List<UUID> allUserIds = userService.findAll()
                .stream().map(User::getId).toList();

        for (UUID userId : allUserIds) {
            Notification notification = Notification.builder()
                    .userId(userId)
                    .type(type)
                    .payload(payload)
                    .build();

            notificationRepository.save(notification);

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + userId,
                    notification
            );
        }
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
