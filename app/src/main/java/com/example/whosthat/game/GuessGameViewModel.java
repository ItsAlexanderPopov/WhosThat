package com.example.whosthat.game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

/**
 * Shared rules for both games: rounds of up to {@link Round#MAX_GUESSES} guesses, a clue row per
 * guess, paid hints, and a run score that keeps growing until a round is lost or skipped
 * (like the streak).
 */
public abstract class GuessGameViewModel<T> extends ViewModel {
    protected final MutableLiveData<Round<T>> round = new MutableLiveData<>();
    protected final MutableLiveData<Integer> streakCounter = new MutableLiveData<>(0);
    protected final MutableLiveData<Integer> score = new MutableLiveData<>(0);
    protected final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    protected final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private boolean clockRunning = false;

    public LiveData<Round<T>> getRound() { return round; }
    public LiveData<Integer> getStreakCounter() { return streakCounter; }
    public LiveData<Integer> getScore() { return score; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    protected abstract List<Attribute<T>> attributes();

    protected abstract List<Hint<T>> hints();

    /** Canonical subject for what the player typed, or null if it isn't a valid name. */
    protected abstract T findSubject(String input);

    protected abstract String nameOf(T subject);

    protected long now() {
        return System.nanoTime() / 1_000_000L;
    }

    /** Starts a new round for {@code target} unless one for it is already in progress. */
    protected void startRoundIfNew(T target) {
        Round<T> current = round.getValue();
        if (current != null && current.isPlaying() && current.getTargetName().equals(nameOf(target))) {
            return;
        }
        Round<T> next = new Round<>(target, nameOf(target), attributes(), hints());
        if (clockRunning) {
            next.resume(now());
        }
        round.setValue(next);
    }

    public GuessOutcome submitGuess(String input) {
        Round<T> current = round.getValue();
        if (current == null || !current.isPlaying()) {
            return GuessOutcome.of(GuessOutcome.Type.NOT_READY);
        }
        T guessed = findSubject(input);
        if (guessed == null) {
            return GuessOutcome.of(GuessOutcome.Type.INVALID);
        }
        String name = nameOf(guessed);
        if (current.hasGuessed(name)) {
            return GuessOutcome.of(GuessOutcome.Type.DUPLICATE);
        }

        boolean correct = name.equals(current.getTargetName());
        current.addGuess(name, guessed, correct, now());
        round.setValue(current);

        switch (current.getState()) {
            case WON: {
                int newScore = value(score) + current.getResult().total;
                score.setValue(newScore);
                streakCounter.setValue(value(streakCounter) + 1);
                return new GuessOutcome(GuessOutcome.Type.WON, current.getTargetName(), current.getResult(),
                        current.guessesLeft(), newScore);
            }
            case LOST:
                return endRun(current);
            default:
                return new GuessOutcome(GuessOutcome.Type.WRONG, null, null, current.guessesLeft(), value(score));
        }
    }

    /** Gives up on the current round; ends the run like running out of guesses. */
    public GuessOutcome skip() {
        Round<T> current = round.getValue();
        if (current == null || !current.isPlaying()) {
            return GuessOutcome.of(GuessOutcome.Type.NOT_READY);
        }
        current.forfeit(now());
        round.setValue(current);
        return endRun(current);
    }

    private GuessOutcome endRun(Round<T> current) {
        int finalScore = value(score);
        score.setValue(0);
        streakCounter.setValue(0);
        return new GuessOutcome(GuessOutcome.Type.LOST, current.getTargetName(), null, 0, finalScore);
    }

    /** Reveals hint {@code index}; returns its value, or null if nothing changed. */
    public String revealHint(int index) {
        Round<T> current = round.getValue();
        if (current == null) {
            return null;
        }
        String value = current.revealHint(index);
        if (value != null) {
            round.setValue(current);
        }
        return value;
    }

    /** Points for a correct guess right now, or -1 when no round is in progress. */
    public int currentPotential() {
        Round<T> current = round.getValue();
        if (current == null || !current.isPlaying()) {
            return -1;
        }
        return current.potential(now()).total;
    }

    public long currentElapsedMs() {
        Round<T> current = round.getValue();
        return current != null ? current.elapsedMs(now()) : 0;
    }

    public void resumeClock() {
        clockRunning = true;
        Round<T> current = round.getValue();
        if (current != null) current.resume(now());
    }

    public void pauseClock() {
        clockRunning = false;
        Round<T> current = round.getValue();
        if (current != null) current.pause(now());
    }

    private static int value(LiveData<Integer> data) {
        Integer v = data.getValue();
        return v != null ? v : 0;
    }
}
