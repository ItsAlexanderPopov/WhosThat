package com.example.whosthat.game;

import java.util.function.Function;

/** Something about the answer the player can reveal for a point cost. */
public final class Hint<T> {
    private final String label;
    private final int cost;
    private final Function<T, String> reveal;

    public Hint(String label, int cost, Function<T, String> reveal) {
        this.label = label;
        this.cost = cost;
        this.reveal = reveal;
    }

    public static <T> Hint<T> of(Attribute<T> attribute, int cost) {
        return new Hint<>(attribute.getLabel(), cost, attribute::format);
    }

    public String getLabel() {
        return label;
    }

    public int getCost() {
        return cost;
    }

    public String reveal(T target) {
        String value = reveal.apply(target);
        return value != null && !value.isEmpty() ? value : Attribute.UNKNOWN_VALUE;
    }
}
