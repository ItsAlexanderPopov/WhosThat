package com.example.whosthat.pokemon;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private static final List<String> GEN1_POKEMON_LIST = Arrays.asList(GEN1_POKEMON_ARRAY);
    private static final List<String> GEN1_POKEMON_LIST_LOWERCASE = GEN1_POKEMON_LIST.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());

    public static String[] getGen1PokemonArray() {
        return GEN1_POKEMON_ARRAY.clone();
    }

    public static List<String> getGen1PokemonList() {
        return GEN1_POKEMON_LIST;
    }

    public static String getPokemonByNumber(int number) {
        if (number < 1 || number > 151) {
            return null;
        }
        return GEN1_POKEMON_ARRAY[number - 1];
    }

    public static boolean isGen1Pokemon(String name) {
        return GEN1_POKEMON_LIST_LOWERCASE.contains(name.toLowerCase());
    }

    public static String normalizePokemonName(String name) {
        return name.toLowerCase()
                .replace("♀", "-f")
                .replace("♂", "-m")
                .replace("'", "")
                .replace(". ", "-")
                .replace(" ", "-");
    }

    public static List<String> getFormattedPokemonList() {
        return GEN1_POKEMON_LIST;
    }

    public static String denormalizePokemonName(String normalizedName) {
        String denormalized = normalizedName
                .replace("-f", "♀")
                .replace("-m", "♂")
                .replace("-", " ");
        return denormalized.substring(0, 1).toUpperCase() + denormalized.substring(1);
    }
}