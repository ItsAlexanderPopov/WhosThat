package com.example.whosthat.pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Pokedle-style facts about a Pokemon, used for clue rows and hints. */
public final class PokemonProfile {
    public final int number;
    public final String name;
    public final String type1;
    /** Null for single-type Pokemon. */
    public final String type2;
    public final String habitat;
    public final String color;
    /** 1 = basic, 2 = first evolution, 3 = second evolution (counting evolutions from later generations). */
    public final int evolutionStage;
    public final double heightM;
    public final double weightKg;

    PokemonProfile(int number, String name, String type1, String type2, String habitat, String color,
                   int evolutionStage, double heightM, double weightKg) {
        this.number = number;
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
        this.habitat = habitat;
        this.color = color;
        this.evolutionStage = evolutionStage;
        this.heightM = heightM;
        this.weightKg = weightKg;
    }

    public String type2OrNone() {
        return type2 != null ? type2 : "None";
    }

    public List<String> types() {
        List<String> types = new ArrayList<>();
        types.add(type1);
        if (type2 != null) types.add(type2);
        return Collections.unmodifiableList(types);
    }
}
