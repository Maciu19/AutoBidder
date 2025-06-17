package com.maciu19.autobidder.api.auction.dto;

import com.maciu19.autobidder.api.auction.model.FileType;

import java.time.Instant;
import java.util.UUID;

public record MediaAssetDto(
        Long id,
        UUID auctionId,
        String fileUrl,
        FileType fileType,
        String title,
        int displayOrder,
        Instant createdDate
) { }
