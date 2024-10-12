package com.example.whosthat.league;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChampionList {
    private static List<String> championNames = new ArrayList<>();

    public static void initialize(Map<String, LeagueChampionModel.ChampionData> champions) {
        championNames.clear();
        for (LeagueChampionModel.ChampionData champion : champions.values()) {
            championNames.add(champion.getName());
        }
    }

    public static List<String> getChampionNames() {
        return new ArrayList<>(championNames);
    }

    public static boolean isValidChampion(String name) {
        return championNames.stream()
                .anyMatch(champion -> champion.equalsIgnoreCase(name));
    }

    public static String normalizeChampionName(String name) {
        return name.toLowerCase().replace(" ", "").replace("'", "");
    }

    public static boolean isInitialized() {
        return !championNames.isEmpty();
    }
}