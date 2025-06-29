package com.maciu19.autobidder.api.notification.service;

import com.maciu19.autobidder.api.bid.dto.BidPlacedEvent;
import com.maciu19.autobidder.api.config.RabbitMQConfig;
import com.maciu19.autobidder.api.notification.model.Notification;
import com.maciu19.autobidder.api.notification.model.NotificationType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleBidPlacedEvent(BidPlacedEvent event) {
        Notification sellerNotification = notificationService.createAndSaveNotification(
                event.sellerId(),
                NotificationType.NEW_BID,
                "A new bid of " + event.bidAmount() + "€ was placed on your auction '" + event.auctionTitle() + "'."
        );

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + sellerNotification.getUserId().toString(),
                sellerNotification
        );

        if (event.outbidderId() != null) {
            Notification outbidNotification = notificationService.createAndSaveNotification(
                    event.outbidderId(),
                    NotificationType.OUTBID,
                    "You have been outbid on '" + event.auctionTitle() + "'."
            );

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + outbidNotification.getUserId().toString(),
                    outbidNotification
            );
        }
    }
}
