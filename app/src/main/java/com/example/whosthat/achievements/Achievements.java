package com.example.whosthat.achievements;

import com.example.whosthat.GameMode;
import com.example.whosthat.HighScoreManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** The list of achievements and the rules for unlocking them. */
public final class Achievements {
    public static final int[] STREAK_TIERS = {5, 10, 15, 20};
    public static final int[] SCORE_TIERS = {5_000, 10_000, 25_000};
    public static final int MIND_READER_COUNT = 10;

    private Achievements() {
    }

    /** Fresh, all-locked list in display order: League, then Pokemon, then the secret one. */
    public static List<AchievementModel> createAll() {
        List<AchievementModel> list = new ArrayList<>();
        for (GameMode game : new GameMode[]{GameMode.LEAGUE, GameMode.POKEMON}) {
            String name = game.displayName;
            for (int streak : STREAK_TIERS) {
                // Ids of the streak achievements match their existing icons, e.g. "i5lol"
                list.add(new AchievementModel("i" + streak + game.key,
                        String.format(Locale.US, "Score %d streak points in %s", streak, name),
                        AchievementModel.Kind.STREAK, streak, game));
            }
            for (int tier = 0; tier < SCORE_TIERS.length; tier++) {
                list.add(new AchievementModel("ach_score" + (tier + 1) + "_" + game.key,
                        String.format(Locale.US, "Score %,d points in one %s run", SCORE_TIERS[tier], name),
                        AchievementModel.Kind.BEST_SCORE, SCORE_TIERS[tier], game));
            }
            list.add(new AchievementModel("ach_firsttry_" + game.key,
                    "Sharp eye: guess a " + subject(game) + " on the first try",
                    AchievementModel.Kind.FIRST_TRY_COUNT, 1, game));
            list.add(new AchievementModel("ach_firsttry10_" + game.key,
                    String.format(Locale.US, "Mind reader: %d first-try guesses in %s", MIND_READER_COUNT, name),
                    AchievementModel.Kind.FIRST_TRY_COUNT, MIND_READER_COUNT, game));
            list.add(new AchievementModel("ach_lightning_" + game.key,
                    String.format(Locale.US, "Lightning: guess a %s in under %d seconds",
                            subject(game), HighScoreManager.LIGHTNING_SECONDS),
                    AchievementModel.Kind.LIGHTNING, 0, game));
            list.add(new AchievementModel("ach_clutch_" + game.key,
                    "Clutch: guess a " + subject(game) + " with your last guess",
                    AchievementModel.Kind.CLUTCH, 0, game));
        }
        list.add(new AchievementModel("secret", "Secret Achievement", AchievementModel.Kind.SECRET, 0, null));
        return list;
    }

    private static String subject(GameMode game) {
        return game == GameMode.POKEMON ? "Pokemon" : "champion";
    }

    public static boolean isUnlocked(AchievementModel achievement, HighScoreManager scores) {
        GameMode game = achievement.getGame();
        switch (achievement.getKind()) {
            case STREAK:
                return scores.getHighStreak(game) >= achievement.getRequiredScore();
            case BEST_SCORE:
                return scores.getBestScore(game) >= achievement.getRequiredScore();
            case FIRST_TRY_COUNT:
                return scores.getFirstTryCount(game) >= achievement.getRequiredScore();
            case LIGHTNING:
                return scores.isLightningUnlocked(game);
            case CLUTCH:
                return scores.isClutchUnlocked(game);
            case SECRET:
                return scores.isSecretAchievementUnlocked();
            default:
                return false;
        }
    }

    /** Updates each achievement's unlocked state (and the secret one's icon and text). */
    public static void refresh(List<AchievementModel> achievements, HighScoreManager scores) {
        for (AchievementModel achievement : achievements) {
            boolean unlocked = isUnlocked(achievement, scores);
            achievement.setUnlocked(unlocked);
            if (achievement.getKind() == AchievementModel.Kind.SECRET) {
                achievement.setIconName(unlocked ? "next" : "secret");
                achievement.setDescription(unlocked ? "Discover secret keyword of \"next\"" : "Secret Achievement");
            }
        }
    }

    /** Ids of the achievements unlocked right now. */
    public static Set<String> unlockedIds(HighScoreManager scores) {
        Set<String> ids = new HashSet<>();
        for (AchievementModel achievement : createAll()) {
            if (isUnlocked(achievement, scores)) {
                ids.add(achievement.getId());
            }
        }
        return ids;
    }

    /** Achievements unlocked now that weren't in {@code before}, with their final descriptions. */
    public static List<AchievementModel> newlyUnlocked(Set<String> before, HighScoreManager scores) {
        List<AchievementModel> all = createAll();
        refresh(all, scores);
        List<AchievementModel> fresh = new ArrayList<>();
        for (AchievementModel achievement : all) {
            if (achievement.isUnlocked() && !before.contains(achievement.getId())) {
                fresh.add(achievement);
            }
        }
        return fresh;
    }
}
