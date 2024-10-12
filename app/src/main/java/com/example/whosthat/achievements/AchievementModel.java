package com.example.whosthat.achievements;

public class AchievementModel {
    private String iconName;
    private String description;
    private boolean isUnlocked;
    private int requiredScore;
    private String gameType;

    public AchievementModel(String iconName, String description, int requiredScore, String gameType) {
        this.iconName = iconName;
        this.description = description;
        this.requiredScore = requiredScore;
        this.gameType = gameType;
        this.isUnlocked = false;
    }

    // Getters and setters
    public String getIconName() { return iconName; }
    public String getDescription() { return description; }
    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
    public int getRequiredScore() { return requiredScore; }
    public String getGameType() { return gameType; }
}