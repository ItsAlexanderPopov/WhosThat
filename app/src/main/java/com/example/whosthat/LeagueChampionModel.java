package com.example.whosthat;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class LeagueChampionModel {
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    public static class ChampionList {
        @SerializedName("data")
        public Map<String, LeagueChampionModel> champions;
    }
}