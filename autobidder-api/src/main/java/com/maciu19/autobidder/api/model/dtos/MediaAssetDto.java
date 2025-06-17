package com.maciu19.autobidder.api.model.dtos;

import com.maciu19.autobidder.api.model.enums.FileType;

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
