package com.maciu19.autobidder.api.auction.dto;

import com.maciu19.autobidder.api.auction.model.AuctionStatus;
import com.maciu19.autobidder.api.user.dto.UserDto;

import java.math.BigDecimal;
import java.util.UUID;

public record AuctionStatusUpdateDto(
        UUID auctionId,
        BigDecimal currentPrice,
        UserDto winningUser,
        int bidCount,
        AuctionStatus status
) { }
