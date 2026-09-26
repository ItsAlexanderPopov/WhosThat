package com.example.whosthat.game;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.whosthat.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

/**
 * Draws the shared game UI (layouts game_stats and game_board): stats, hint chips and the
 * Pokedle/Loldle-style board with one row of colored tiles per guess, newest first.
 */
public final class GameBoardUi<T> {
    public interface HintListener {
        void onHintClicked(int index);
    }

    private static final int NAME_TILE_WIDTH_DP = 88;
    private static final int TILE_WIDTH_DP = 66;
    private static final int TILE_HEIGHT_DP = 52;
    private static final int TILE_MARGIN_DP = 2;

    private final Context context;
    private final HintListener hintListener;
    private final TextView streakCounter;
    private final TextView guessesLeft;
    private final TextView scoreCounter;
    private final TextView roundTimer;
    private final TextView bestScore;
    private final ChipGroup hintChips;
    private final LinearLayout guessBoard;
    private final TextView guessBoardEmpty;

    public GameBoardUi(Activity activity, HintListener hintListener) {
        this.context = activity;
        this.hintListener = hintListener;
        streakCounter = activity.findViewById(R.id.streak_counter);
        guessesLeft = activity.findViewById(R.id.guesses_left);
        scoreCounter = activity.findViewById(R.id.score_counter);
        roundTimer = activity.findViewById(R.id.round_timer);
        bestScore = activity.findViewById(R.id.best_score);
        hintChips = activity.findViewById(R.id.hint_chips);
        guessBoard = activity.findViewById(R.id.guess_board);
        guessBoardEmpty = activity.findViewById(R.id.guess_board_empty);
    }

    public void setStreak(int streak) {
        streakCounter.setText(String.valueOf(streak));
    }

    public void setScore(int score, int best) {
        scoreCounter.setText(context.getString(R.string.score_format, score));
        bestScore.setText(context.getString(R.string.best_format, Math.max(score, best)));
    }

    /** @param potential points for a correct guess now, or negative when no round is running */
    public void updateTimer(long elapsedMs, int potential) {
        if (potential < 0) {
            roundTimer.setText(R.string.timer_idle);
        } else {
            roundTimer.setText(context.getString(R.string.timer_format, (int) (elapsedMs / 1000), potential));
        }
    }

    public void bindRound(Round<T> round) {
        if (round == null) {
            guessesLeft.setText(context.getString(R.string.guesses_left_format, Round.MAX_GUESSES, Round.MAX_GUESSES));
            hintChips.removeAllViews();
            guessBoard.removeAllViews();
            guessBoardEmpty.setVisibility(View.VISIBLE);
            return;
        }
        guessesLeft.setText(context.getString(R.string.guesses_left_format, round.guessesLeft(), Round.MAX_GUESSES));
        bindHints(round);
        bindBoard(round);
    }

    private void bindHints(Round<T> round) {
        hintChips.removeAllViews();
        List<Hint<T>> hints = round.getHints();
        for (int i = 0; i < hints.size(); i++) {
            Hint<T> hint = hints.get(i);
            Chip chip = new Chip(context);
            chip.setEnsureMinTouchTargetSize(false);
            int background;
            int text;
            if (round.isHintRevealed(i)) {
                chip.setText(hint.getLabel() + ": " + round.getRevealedHint(i));
                background = R.color.gold;
                text = R.color.black;
                chip.setClickable(false);
            } else if (!round.isPlaying()) {
                // Round over: show everything for free so the player learns the answer's facts
                chip.setText(hint.getLabel() + ": " + hint.reveal(round.getTarget()));
                background = R.color.gray;
                text = R.color.beige;
                chip.setClickable(false);
            } else {
                chip.setText(hint.getLabel() + "  −" + hint.getCost());
                background = R.color.blue;
                text = R.color.beige;
                final int index = i;
                chip.setOnClickListener(v -> hintListener.onHintClicked(index));
            }
            chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, background)));
            chip.setTextColor(ContextCompat.getColor(context, text));
            chip.setChipStrokeWidth(0f);
            hintChips.addView(chip);
        }
    }

    private void bindBoard(Round<T> round) {
        guessBoard.removeAllViews();
        List<GuessResult> guesses = round.getGuesses();
        guessBoardEmpty.setVisibility(guesses.isEmpty() ? View.VISIBLE : View.GONE);
        if (guesses.isEmpty()) {
            return;
        }

        LinearLayout header = newRow();
        header.addView(headerTile("", NAME_TILE_WIDTH_DP));
        for (Attribute<T> attribute : round.getAttributes()) {
            header.addView(headerTile(attribute.getLabel(), TILE_WIDTH_DP));
        }
        guessBoard.addView(header);

        for (int i = guesses.size() - 1; i >= 0; i--) {
            GuessResult guess = guesses.get(i);
            LinearLayout row = newRow();
            row.addView(tile(guess.name, guess.correct ? Match.CORRECT : Match.WRONG, NAME_TILE_WIDTH_DP, true));
            for (Clue clue : guess.clues) {
                row.addView(tile(clue.displayValue(), clue.match, TILE_WIDTH_DP, false));
            }
            guessBoard.addView(row);
        }
    }

    private LinearLayout newRow() {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        return row;
    }

    private TextView headerTile(String text, int widthDp) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(widthDp), LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(dp(TILE_MARGIN_DP), 0, dp(TILE_MARGIN_DP), dp(TILE_MARGIN_DP));
        view.setLayoutParams(params);
        return view;
    }

    private TextView tile(String text, Match match, int widthDp, boolean bold) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        view.setTextColor(ContextCompat.getColor(context, R.color.white));
        view.setGravity(Gravity.CENTER);
        view.setMaxLines(3);
        view.setPadding(dp(2), dp(2), dp(2), dp(2));
        if (bold) {
            view.setTypeface(Typeface.DEFAULT_BOLD);
        }

        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(dp(6));
        background.setColor(ContextCompat.getColor(context, colorFor(match)));
        view.setBackground(background);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(widthDp), dp(TILE_HEIGHT_DP));
        params.setMargins(dp(TILE_MARGIN_DP), dp(TILE_MARGIN_DP), dp(TILE_MARGIN_DP), dp(TILE_MARGIN_DP));
        view.setLayoutParams(params);
        return view;
    }

    private static int colorFor(Match match) {
        switch (match) {
            case CORRECT:
                return R.color.clue_correct;
            case PARTIAL:
                return R.color.clue_partial;
            case WRONG:
                return R.color.clue_wrong;
            default:
                return R.color.clue_unknown;
        }
    }

    private int dp(int value) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                context.getResources().getDisplayMetrics()));
    }
}
