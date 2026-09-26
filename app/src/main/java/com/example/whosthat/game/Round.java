package com.example.whosthat.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * State of one round: the answer, guesses so far, revealed hints and the round clock.
 * The clock only runs while resumed, so time spent outside the game screen doesn't count.
 */
public final class Round<T> {
    public enum State { PLAYING, WON, LOST }

    public static final int MAX_GUESSES = 6;

    private final T target;
    private final String targetName;
    private final List<Attribute<T>> attributes;
    private final List<Hint<T>> hints;
    private final List<GuessResult> guesses = new ArrayList<>();
    private final String[] revealedHints;

    private State state = State.PLAYING;
    private long accumulatedMs = 0;
    private long resumedAt = -1;
    private Scoring.Breakdown result;

    public Round(T target, String targetName, List<Attribute<T>> attributes, List<Hint<T>> hints) {
        this.target = target;
        this.targetName = targetName;
        this.attributes = attributes;
        this.hints = hints;
        this.revealedHints = new String[hints.size()];
    }

    public T getTarget() { return target; }
    public String getTargetName() { return targetName; }
    public List<Attribute<T>> getAttributes() { return attributes; }
    public List<Hint<T>> getHints() { return hints; }
    public State getState() { return state; }
    public boolean isPlaying() { return state == State.PLAYING; }
    public Scoring.Breakdown getResult() { return result; }

    /** Guesses in the order they were made. */
    public List<GuessResult> getGuesses() {
        return Collections.unmodifiableList(guesses);
    }

    public void resume(long nowMs) {
        if (resumedAt < 0 && state == State.PLAYING) {
            resumedAt = nowMs;
        }
    }

    public void pause(long nowMs) {
        if (resumedAt >= 0) {
            accumulatedMs += Math.max(0, nowMs - resumedAt);
            resumedAt = -1;
        }
    }

    public long elapsedMs(long nowMs) {
        return accumulatedMs + (resumedAt >= 0 ? Math.max(0, nowMs - resumedAt) : 0);
    }

    public boolean hasGuessed(String name) {
        for (GuessResult g : guesses) {
            if (g.name.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public int wrongGuesses() {
        int wrong = 0;
        for (GuessResult g : guesses) {
            if (!g.correct) wrong++;
        }
        return wrong;
    }

    public int guessesLeft() {
        return MAX_GUESSES - guesses.size();
    }

    public GuessResult addGuess(String name, T guessed, boolean correct, long nowMs) {
        if (state != State.PLAYING) {
            throw new IllegalStateException("Round is over");
        }
        List<Clue> clues = new ArrayList<>();
        for (Attribute<T> attribute : attributes) {
            clues.add(attribute.compare(guessed, target));
        }
        GuessResult guess = new GuessResult(name, clues, correct);
        guesses.add(guess);

        if (correct) {
            result = Scoring.calculate(elapsedMs(nowMs), wrongGuesses(), hintCost());
            state = State.WON;
            pause(nowMs);
        } else if (guesses.size() >= MAX_GUESSES) {
            state = State.LOST;
            pause(nowMs);
        }
        return guess;
    }

    /** Gives up on the round (the "next" keyword). */
    public void forfeit(long nowMs) {
        if (state == State.PLAYING) {
            state = State.LOST;
            pause(nowMs);
        }
    }

    public boolean isHintRevealed(int index) {
        return revealedHints[index] != null;
    }

    /** Returns the revealed hint value, or null if it was already revealed or the round is over. */
    public String revealHint(int index) {
        if (state != State.PLAYING || revealedHints[index] != null) {
            return null;
        }
        revealedHints[index] = hints.get(index).reveal(target);
        return revealedHints[index];
    }

    public String getRevealedHint(int index) {
        return revealedHints[index];
    }

    public int hintCost() {
        int cost = 0;
        for (int i = 0; i < hints.size(); i++) {
            if (revealedHints[i] != null) cost += hints.get(i).getCost();
        }
        return cost;
    }

    /** Points the player would get if the next guess were correct. */
    public Scoring.Breakdown potential(long nowMs) {
        return Scoring.calculate(elapsedMs(nowMs), wrongGuesses(), hintCost());
    }
}
