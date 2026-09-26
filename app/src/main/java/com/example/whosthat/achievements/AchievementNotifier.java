package com.example.whosthat.achievements;

import android.app.Activity;
import android.content.Intent;

import com.example.whosthat.HighScoreManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Set;

/** Records progress and tells the player about achievements it unlocked. */
public final class AchievementNotifier {
    private AchievementNotifier() {
    }

    /** Runs {@code record} (which saves progress) and announces any achievements it unlocked. */
    public static void recordAndNotify(Activity activity, HighScoreManager scores, Runnable record) {
        Set<String> before = Achievements.unlockedIds(scores);
        record.run();
        show(activity, Achievements.newlyUnlocked(before, scores));
    }

    private static void show(Activity activity, List<AchievementModel> unlocked) {
        if (unlocked.isEmpty()) {
            return;
        }
        StringBuilder text = new StringBuilder(unlocked.size() == 1
                ? "🏆 Achievement unlocked!" : "🏆 " + unlocked.size() + " achievements unlocked!");
        for (AchievementModel achievement : unlocked) {
            text.append('\n').append(achievement.getDescription());
        }
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);
        snackbar.setTextMaxLines(1 + unlocked.size());
        snackbar.setAction("View", v -> activity.startActivity(new Intent(activity, AchievementsActivity.class)));
        snackbar.show();
    }
}
