package com.maciu19.autobidder.api.auction.dto;

import com.maciu19.autobidder.api.user.dto.UserDto;

import java.math.BigDecimal;
import java.util.UUID;

public record AuctionPriceUpdateDto (
        UUID auctionId,
        BigDecimal newCurrentPrice,
        UserDto winningUser,
        int totalBids
) {
}
