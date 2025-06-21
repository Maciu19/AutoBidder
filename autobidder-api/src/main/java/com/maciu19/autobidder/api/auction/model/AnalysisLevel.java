package com.maciu19.autobidder.api.auction.model;

public enum AnalysisLevel {
    ENGINE("Engine-Level Analysis", 0.95, 0.5),
    GENERATION("Generation-Level Analysis", 0.80, 0.75),
    MODEL("Model-Level Analysis", 0.65, 1.0);

    public final String displayName;
    public final double confidenceScore;
    public final double rangeMultiplier;

    AnalysisLevel(String displayName, double confidenceScore, double rangeMultiplier) {
        this.displayName = displayName;
        this.confidenceScore = confidenceScore;
        this.rangeMultiplier = rangeMultiplier;
    }
}
