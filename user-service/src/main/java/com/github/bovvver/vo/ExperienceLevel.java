package com.github.bovvver.vo;

public enum ExperienceLevel {
    BEGINNER(0),
    EXPERIENCED(1000),
    EXPERT(5000),
    PROFESSIONAL(15000);

    private final int minScore;

    ExperienceLevel(int minScore) {
        this.minScore = minScore;
    }

    /**
     * Determines the ExperienceLevel for a given score by finding
     * the highest level whose minScore threshold is met.
     */
    public static ExperienceLevel fromScore(int score) {
        ExperienceLevel result = BEGINNER;
        for (ExperienceLevel level : values()) {
            if (score < level.minScore) {
                break;
            }
            result = level;
        }
        return result;
    }
}
