package com.maciu19.autobidder.api.auction.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionPublishDto(
    UUID auctionId,
    LocalDateTime startTime,
    LocalDateTime endTime)
{ }
