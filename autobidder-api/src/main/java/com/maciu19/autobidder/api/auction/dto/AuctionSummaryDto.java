package com.maciu19.autobidder.api.auction.dto;

import com.maciu19.autobidder.api.auction.model.AuctionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionSummaryDto (
    UUID id,
    String title,
    AuctionStatus status,
    BigDecimal startingPrice,
    BigDecimal currentPrice,
    LocalDateTime endTime,
    String thumbnailUrl,
    int totalBids
) { }
