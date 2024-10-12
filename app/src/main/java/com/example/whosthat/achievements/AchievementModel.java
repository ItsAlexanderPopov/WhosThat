package com.example.whosthat.achievements;

public class AchievementModel {
    private String id;
    private String description;
    private int requiredScore;
    private String gameType;
    private boolean isUnlocked;
    private String iconName;

    public AchievementModel(String id, String description, int requiredScore, String gameType) {
        this.id = id;
        this.description = description;
        this.requiredScore = requiredScore;
        this.gameType = gameType;
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

    public int getRequiredScore() {
        return requiredScore;
    }

    public String getGameType() {
        return gameType;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }
}