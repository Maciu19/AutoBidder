package com.maciu19.autobidder.api.bid.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BidPlacedEvent(
        UUID sellerId,
        UUID outbidderId,
        BigDecimal bidAmount,
        String auctionTitle
) {}
