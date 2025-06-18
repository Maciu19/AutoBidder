package com.maciu19.autobidder.api.bid.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BidDto (
        UUID id,
        UUID auctionId,
        UUID userId,
        BigDecimal bidAmount,
        Instant createdDate,
        Instant lastModifiedDate
) {  }
