package com.maciu19.autobidder.api.auction.dto;

import com.maciu19.autobidder.api.auction.model.AuctionStatus;
import com.maciu19.autobidder.api.user.dto.UserDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionSummaryDto (
    UUID id,
    String title,
    UserDto seller,
    UserDto winningUser,
    AuctionStatus status,
    BigDecimal startingPrice,
    BigDecimal currentPrice,
    LocalDateTime endTime,
    String thumbnailUrl,
    int totalBids
) { }
