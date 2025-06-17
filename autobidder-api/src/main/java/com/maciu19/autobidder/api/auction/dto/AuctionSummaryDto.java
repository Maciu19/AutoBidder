package com.maciu19.autobidder.api.auction.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionSummaryDto (
    UUID id,
    String title,
    Double startingPrice,
    LocalDateTime endTime,
    String thumbnailUrl
) { }
