package com.github.bovvver.vo;

public record Score(int value) {

    public Score {
        if (value < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
        }
    }

    public static Score of(int value) {
        return new Score(value);
    }
}
