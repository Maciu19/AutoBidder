package com.maciu19.autobidder.api.notification.service;

import com.maciu19.autobidder.api.notification.model.Notification;
import com.maciu19.autobidder.api.notification.model.NotificationType;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    Notification createAndSaveNotification(UUID userId, NotificationType type, String payload);

    void markNotificationAsRead(UUID userId, UUID notificationId);

    List<Notification> getUnreadNotificationsForUser(UUID userId);
}
