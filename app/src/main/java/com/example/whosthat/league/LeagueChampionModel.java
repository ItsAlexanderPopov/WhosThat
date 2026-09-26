package com.example.whosthat.league;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class LeagueChampionModel {
    public static class ChampionData {
        // Data Dragon id used in asset URLs, e.g. "MonkeyKing" for Wukong
        @SerializedName("id")
        private String id;

        // Display name, e.g. "Wukong", "Nunu & Willump"
        @SerializedName("name")
        private String name;

        // e.g. "the Nine-Tailed Fox"
        @SerializedName("title")
        private String title;

        // Resource bar, e.g. "Mana", "Energy", "None", "Fury"
        @SerializedName("partype")
        private String partype;

        @SerializedName("image")
        private Image image;

        @SerializedName("stats")
        private Stats stats;

        public ChampionData() {
        }

        public ChampionData(String id, String name, String imageFile) {
            this.id = id;
            this.name = name;
            this.image = new Image();
            this.image.full = imageFile;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Image getImage() {
            return image;
        }

        public String getTitle() {
            return title;
        }

        public String getPartype() {
            return partype;
        }

        public Stats getStats() {
            return stats;
        }
    }

    public static class Stats {
        @SerializedName("attackrange")
        private double attackRange;

        public double getAttackRange() {
            return attackRange;
        }
    }

    public static class Image {
        // File name of the square portrait, e.g. "MonkeyKing.png"
        @SerializedName("full")
        private String full;

        public String getFull() {
            return full;
        }
    }

    public static class ChampionList {
        @SerializedName("version")
        private String version;

        @SerializedName("data")
        private Map<String, ChampionData> champions;

        public String getVersion() {
            return version;
        }

        public Map<String, ChampionData> getChampions() {
            return champions;
        }
    }
}
