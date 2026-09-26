package com.example.whosthat.achievements;

import com.example.whosthat.GameMode;

public class AchievementModel {
    /** What has to happen to unlock the achievement; {@link #getRequiredScore()} is the threshold. */
    public enum Kind {
        STREAK,
        BEST_SCORE,
        FIRST_TRY_COUNT,
        LIGHTNING,
        CLUTCH,
        SECRET
    }

    private final String id;
    private String description;
    private final Kind kind;
    private final int requiredScore;
    /** Null for achievements that aren't tied to one game (the secret one). */
    private final GameMode game;
    private boolean isUnlocked;
    private String iconName;

    public AchievementModel(String id, String description, Kind kind, int requiredScore, GameMode game) {
        this.id = id;
        this.description = description;
        this.kind = kind;
        this.requiredScore = requiredScore;
        this.game = game;
        this.isUnlocked = false;
        this.iconName = id;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Kind getKind() {
        return kind;
    }

    public int getRequiredScore() {
        return requiredScore;
    }

    public GameMode getGame() {
        return game;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }
}
