package com.maciu19.autobidder.api.notification.model;

public enum NotificationType {
    NEW_BID,           // A new bid has been placed on an auction.
    OUTBID,            // You have been outbid by another user.
    AUCTION_WON,       // You have won an auction.
    AUCTION_LOST,      // The auction you were bidding on has ended, and you did not win.
    AUCTION_CREATED,   // A new auction you might be interested in has been created.
    AUCTION_ENDED,     // An auction has finished.
    AUCTION_CANCELLED, // An auction has been cancelled.
}
