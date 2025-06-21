package com.maciu19.autobidder.api.auction.dto;

import java.math.BigDecimal;

public record PriceRecommendationDto(
        int carsFound,
        String analysisLevel,
        double confidenceScore,
        BigDecimal averagePrice,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal recommendedPrice,
        BigDecimal fairMarketRangeStart,
        BigDecimal fairMarketRangeEnd
) {
    public static PriceRecommendationDto notEnoughData() {
        return new PriceRecommendationDto(
                0,
                "Insufficient Data",
                0.0,
                null,
                null,
                null,
                null,
                null,
                null);
    }
}
