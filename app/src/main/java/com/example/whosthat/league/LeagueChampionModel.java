package com.example.whosthat.league;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class LeagueChampionModel {
    public static class ChampionData {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("skins")
        private List<Skin> skins;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public List<Skin> getSkins() {
            return skins;
        }
    }

    public static class Skin {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("num")
        private int num;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getNum() {
            return num;
        }
    }

    public static class ChampionList {
        @SerializedName("data")
        private Map<String, ChampionData> champions;

        public Map<String, ChampionData> getChampions() {
            return champions;
        }
    }

    @SerializedName("data")
    private Map<String, ChampionData> data;

    public Map<String, ChampionData> getData() {
        return data;
    }
}