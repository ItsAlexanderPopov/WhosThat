package com.example.whosthat;

import java.util.Arrays;
import java.util.List;

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

    /**
     * Returns an array of all Generation 1 Pokémon names.
     * @return String array containing 151 Pokémon names
     */
    public static String[] getGen1PokemonArray() {
        return GEN1_POKEMON_ARRAY.clone(); // Return a clone to prevent modification of the original array
    }

    /**
     * Returns a List of all Generation 1 Pokémon names.
     * @return List<String> containing 151 Pokémon names
     */
    public static List<String> getGen1PokemonList() {
        return GEN1_POKEMON_LIST;
    }

    /**
     * Returns the name of a Generation 1 Pokémon by its Pokédex number.
     * @param number The Pokédex number (1-151)
     * @return The Pokémon's name, or null if the number is out of range
     */
    public static String getPokemonByNumber(int number) {
        if (number < 1 || number > 151) {
            return null;
        }
        return GEN1_POKEMON_ARRAY[number - 1];
    }

    /**
     * Checks if a given name is a Generation 1 Pokémon.
     * @param name The name to check
     * @return true if the name is a Gen 1 Pokémon, false otherwise
     */
    public static boolean isGen1Pokemon(String name) {
        return GEN1_POKEMON_LIST.contains(name);
    }
}