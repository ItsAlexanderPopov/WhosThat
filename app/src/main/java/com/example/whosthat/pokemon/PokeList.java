package com.example.whosthat.pokemon;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PokeList {
    private static final String[] GEN1_POKEMON_ARRAY = {
            "Bulbasaur", "Ivysaur", "Venusaur", "Charmander", "Charmeleon", "Charizard", "Squirtle", "Wartortle", "Blastoise", "Caterpie",
            "Metapod", "Butterfree", "Weedle", "Kakuna", "Beedrill", "Pidgey", "Pidgeotto", "Pidgeot", "Rattata", "Raticate",
            "Spearow", "Fearow", "Ekans", "Arbok", "Pikachu", "Raichu", "Sandshrew", "Sandslash", "Nidoran♀", "Nidorina",
            "Nidoqueen", "Nidoran♂", "Nidorino", "Nidoking", "Clefairy", "Clefable", "Vulpix", "Ninetales", "Jigglypuff", "Wigglytuff",
            "Zubat", "Golbat", "Oddish", "Gloom", "Vileplume", "Paras", "Parasect", "Venonat", "Venomoth", "Diglett",
            "Dugtrio", "Meowth", "Persian", "Psyduck", "Golduck", "Mankey", "Primeape", "Growlithe", "Arcanine", "Poliwag",
            "Poliwhirl", "Poliwrath", "Abra", "Kadabra", "Alakazam", "Machop", "Machoke", "Machamp", "Bellsprout", "Weepinbell",
            "Victreebel", "Tentacool", "Tentacruel", "Geodude", "Graveler", "Golem", "Ponyta", "Rapidash", "Slowpoke", "Slowbro",
            "Magnemite", "Magneton", "Farfetch'd", "Doduo", "Dodrio", "Seel", "Dewgong", "Grimer", "Muk", "Shellder",
            "Cloyster", "Gastly", "Haunter", "Gengar", "Onix", "Drowzee", "Hypno", "Krabby", "Kingler", "Voltorb",
            "Electrode", "Exeggcute", "Exeggutor", "Cubone", "Marowak", "Hitmonlee", "Hitmonchan", "Lickitung", "Koffing", "Weezing",
            "Rhyhorn", "Rhydon", "Chansey", "Tangela", "Kangaskhan", "Horsea", "Seadra", "Goldeen", "Seaking", "Staryu",
            "Starmie", "Mr. Mime", "Scyther", "Jynx", "Electabuzz", "Magmar", "Pinsir", "Tauros", "Magikarp", "Gyarados",
            "Lapras", "Ditto", "Eevee", "Vaporeon", "Jolteon", "Flareon", "Porygon", "Omanyte", "Omastar", "Kabuto",
            "Kabutops", "Aerodactyl", "Snorlax", "Articuno", "Zapdos", "Moltres", "Dratini", "Dragonair", "Dragonite", "Mewtwo", "Mew"
    };

    public static final int GEN1_COUNT = GEN1_POKEMON_ARRAY.length;

    private static final List<String> GEN1_POKEMON_LIST = Collections.unmodifiableList(Arrays.asList(GEN1_POKEMON_ARRAY));

    public static String[] getGen1PokemonArray() {
        return GEN1_POKEMON_ARRAY.clone();
    }

    public static List<String> getGen1PokemonList() {
        return GEN1_POKEMON_LIST;
    }

    public static String getPokemonByNumber(int number) {
        if (number < 1 || number > GEN1_COUNT) {
            return null;
        }
        return GEN1_POKEMON_ARRAY[number - 1];
    }

    /**
     * Comparison key that ignores case, spaces and punctuation, so the display name
     * ("Mr. Mime", "Nidoran♀", "Farfetch'd") and the PokeAPI name ("mr-mime", "nidoran-f",
     * "farfetchd") produce the same key.
     */
    public static String toKey(String name) {
        if (name == null) {
            return "";
        }
        String lower = name.toLowerCase(Locale.ROOT).replace("♀", "f").replace("♂", "m");
        return lower.replaceAll("[^a-z0-9]", "");
    }

    /** Returns the Gen-1 display name matching {@code name} (display or API form), or null. */
    public static String findPokemon(String name) {
        String key = toKey(name);
        if (key.isEmpty()) {
            return null;
        }
        for (String pokemon : GEN1_POKEMON_ARRAY) {
            if (toKey(pokemon).equals(key)) {
                return pokemon;
            }
        }
        return null;
    }

    public static boolean isGen1Pokemon(String name) {
        return findPokemon(name) != null;
    }

    public static boolean isSamePokemon(String a, String b) {
        String keyA = toKey(a);
        return !keyA.isEmpty() && keyA.equals(toKey(b));
    }

    public static List<String> getFormattedPokemonList() {
        return GEN1_POKEMON_LIST;
    }

    /** Turns a PokeAPI name like "mr-mime" into its display name "Mr. Mime". */
    public static String getDisplayName(String apiName) {
        String displayName = findPokemon(apiName);
        if (displayName != null) {
            return displayName;
        }
        if (apiName == null || apiName.isEmpty()) {
            return "";
        }
        return apiName.substring(0, 1).toUpperCase(Locale.ROOT) + apiName.substring(1).replace("-", " ");
    }
}
