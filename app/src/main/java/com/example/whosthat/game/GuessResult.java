package com.example.whosthat.game;

import java.util.Collections;
import java.util.List;

/** One submitted guess and its row of clues. */
public final class GuessResult {
    public final String name;
    public final List<Clue> clues;
    public final boolean correct;

    public GuessResult(String name, List<Clue> clues, boolean correct) {
        this.name = name;
        this.clues = Collections.unmodifiableList(clues);
        this.correct = correct;
    }
}
