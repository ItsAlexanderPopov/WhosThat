package com.example.whosthat.league;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ChampionList {
    private static final String IMAGE_URL_FORMAT = LeagueRetrofitClient.BASE_URL + "cdn/%s/img/champion/%s";

    private static final List<LeagueChampionModel.ChampionData> champions = new ArrayList<>();
    private static String version;

    public static synchronized void initialize(String ddragonVersion, Collection<LeagueChampionModel.ChampionData> data) {
        champions.clear();
        for (LeagueChampionModel.ChampionData champion : data) {
            if (champion != null && champion.getId() != null && champion.getName() != null) {
                champions.add(champion);
            }
        }
        version = ddragonVersion;
    }

    public static synchronized boolean isInitialized() {
        return !champions.isEmpty();
    }

    public static synchronized List<String> getChampionNames() {
        List<String> names = new ArrayList<>();
        for (LeagueChampionModel.ChampionData champion : champions) {
            names.add(champion.getName());
        }
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        return names;
    }

    /**
     * Finds a champion by display name, ignoring case, spaces and punctuation,
     * so "kaisa", "Kai'Sa" and "KAI SA" all match Kai'Sa.
     */
    public static synchronized LeagueChampionModel.ChampionData findChampion(String name) {
        if (name == null) {
            return null;
        }
        String key = normalizeChampionName(name);
        if (key.isEmpty()) {
            return null;
        }
        for (LeagueChampionModel.ChampionData champion : champions) {
            if (normalizeChampionName(champion.getName()).equals(key)
                    || normalizeChampionName(champion.getId()).equals(key)) {
                return champion;
            }
        }
        return null;
    }

    public static boolean isValidChampion(String name) {
        return findChampion(name) != null;
    }

    /** Picks a random champion, avoiding {@code excludeName} when possible. */
    public static synchronized LeagueChampionModel.ChampionData getRandomChampion(Random random, String excludeName) {
        if (champions.isEmpty()) {
            return null;
        }
        LeagueChampionModel.ChampionData pick = champions.get(random.nextInt(champions.size()));
        if (champions.size() > 1 && pick.getName().equals(excludeName)) {
            pick = champions.get(random.nextInt(champions.size()));
        }
        return pick;
    }

    public static synchronized String getPortraitUrl(LeagueChampionModel.ChampionData champion) {
        String file = champion.getImage() != null && champion.getImage().getFull() != null
                ? champion.getImage().getFull()
                : champion.getId() + ".png";
        return String.format(Locale.US, IMAGE_URL_FORMAT, version, file);
    }

    public static synchronized String getVersion() {
        return version;
    }

    public static String normalizeChampionName(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }
}
