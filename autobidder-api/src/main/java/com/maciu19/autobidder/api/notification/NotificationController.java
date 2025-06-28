package com.maciu19.autobidder.api.notification;

import com.maciu19.autobidder.api.notification.model.Notification;
import com.maciu19.autobidder.api.notification.service.NotificationService;
import com.maciu19.autobidder.api.user.model.User;
import com.maciu19.autobidder.api.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(
            NotificationService notificationService,
            UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        User currentUser = userService.getCurrentUser();
        List<Notification> unreadNotifications = notificationService.getUnreadNotificationsForUser(currentUser.getId());

        return ResponseEntity.ok(unreadNotifications);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable UUID notificationId) {
        User currentUser= userService.getCurrentUser();
        notificationService.markNotificationAsRead(currentUser.getId(), notificationId);

        return ResponseEntity.ok().build();
    }
}