package com.maciu19.autobidder.api.bid.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BidRequestDto (
        UUID auctionId,
        BigDecimal amount
) {  }
