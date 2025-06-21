package com.maciu19.autobidder.api.auction.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AuctionDataPoint (
       BigDecimal price,
       UUID generationId,
       UUID engineId
) {
}
