package com.maciu19.autobidder.api.auction.dto;

import com.maciu19.autobidder.api.auction.model.Feature;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record AuctionUpdateDto(
        UUID auctionId,
        String title,
        String description,
        String location,
        String modifications,
        String knownFlaws,
        String recentServiceHistory,
        String otherItemsIncluded,
        String ownershipHistory,
        Set<Feature> features,
        LocalDateTime endTime
) { }
