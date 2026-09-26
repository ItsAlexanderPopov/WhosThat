package com.example.whosthat.league;

import java.util.Collections;
import java.util.List;

/** Loldle-style facts about a champion: Data Dragon data merged with {@link ChampionAttributes}. */
public final class ChampionProfile {
    private static final double RANGED_MIN_ATTACK_RANGE = 300;

    public final String id;
    public final String name;
    public final String title;
    /** Null when unknown (the champion isn't in {@link ChampionAttributes}). */
    public final String gender;
    public final List<String> positions;
    public final List<String> species;
    public final String resource;
    public final List<String> rangeTypes;
    public final List<String> regions;
    public final Integer releaseYear;

    private ChampionProfile(String id, String name, String title, String gender, List<String> positions,
                            List<String> species, String resource, List<String> rangeTypes,
                            List<String> regions, Integer releaseYear) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.gender = gender;
        this.positions = positions;
        this.species = species;
        this.resource = resource;
        this.rangeTypes = rangeTypes;
        this.regions = regions;
        this.releaseYear = releaseYear;
    }

    public static ChampionProfile from(LeagueChampionModel.ChampionData data) {
        ChampionAttributes.Entry entry = ChampionAttributes.get(data.getId());
        List<String> range = entry != null ? entry.rangeTypes : rangeFromStats(data.getStats());
        return new ChampionProfile(
                data.getId(),
                data.getName(),
                data.getTitle(),
                entry != null ? entry.gender : null,
                entry != null ? entry.positions : null,
                entry != null ? entry.species : null,
                normalizeResource(data.getPartype()),
                range,
                entry != null ? entry.regions : null,
                entry != null ? entry.releaseYear : null);
    }

    static String normalizeResource(String partype) {
        if (partype == null) {
            return null;
        }
        String trimmed = partype.trim();
        if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("None")) {
            return "Manaless";
        }
        return trimmed;
    }

    private static List<String> rangeFromStats(LeagueChampionModel.Stats stats) {
        if (stats == null || stats.getAttackRange() <= 0) {
            return null;
        }
        return Collections.singletonList(stats.getAttackRange() >= RANGED_MIN_ATTACK_RANGE ? "Ranged" : "Melee");
    }
}
