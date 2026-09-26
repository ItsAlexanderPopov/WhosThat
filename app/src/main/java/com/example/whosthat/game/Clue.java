package com.example.whosthat.game;

/** One tile of a guess row: the guessed value and how it compares to the answer. */
public final class Clue {
    public static final String ARROW_HIGHER = "↑";
    public static final String ARROW_LOWER = "↓";

    public final String label;
    public final String value;
    public final Match match;
    /** "↑" when the answer's value is higher, "↓" when lower, "" otherwise. */
    public final String arrow;

    public Clue(String label, String value, Match match, String arrow) {
        this.label = label;
        this.value = value;
        this.match = match;
        this.arrow = arrow;
    }

    public String displayValue() {
        return arrow.isEmpty() ? value : value + " " + arrow;
    }
}
