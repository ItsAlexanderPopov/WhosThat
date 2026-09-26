package com.example.whosthat.pokemon;

/**
 * Gen-1 Pokedex facts, generated from PokeAPI's data files
 * (github.com/PokeAPI/pokeapi, data/v2/csv: pokemon_species, pokemon, pokemon_types,
 * pokemon_colors, pokemon_habitats). Types are the current ones (e.g. Clefairy is Fairy).
 * Entries are in Pokedex order and use the names from {@link PokeList}.
 */
public final class PokedexData {
    private static final PokemonProfile[] PROFILES;

    static {
        Object[][] rows = {
                {"Bulbasaur", "Grass", "Poison", "Grassland", "Green", 1, 0.7, 6.9},
                {"Ivysaur", "Grass", "Poison", "Grassland", "Green", 2, 1.0, 13.0},
                {"Venusaur", "Grass", "Poison", "Grassland", "Green", 3, 2.0, 100.0},
                {"Charmander", "Fire", null, "Mountain", "Red", 1, 0.6, 8.5},
                {"Charmeleon", "Fire", null, "Mountain", "Red", 2, 1.1, 19.0},
                {"Charizard", "Fire", "Flying", "Mountain", "Red", 3, 1.7, 90.5},
                {"Squirtle", "Water", null, "Water's Edge", "Blue", 1, 0.5, 9.0},
                {"Wartortle", "Water", null, "Water's Edge", "Blue", 2, 1.0, 22.5},
                {"Blastoise", "Water", null, "Water's Edge", "Blue", 3, 1.6, 85.5},
                {"Caterpie", "Bug", null, "Forest", "Green", 1, 0.3, 2.9},
                {"Metapod", "Bug", null, "Forest", "Green", 2, 0.7, 9.9},
                {"Butterfree", "Bug", "Flying", "Forest", "White", 3, 1.1, 32.0},
                {"Weedle", "Bug", "Poison", "Forest", "Brown", 1, 0.3, 3.2},
                {"Kakuna", "Bug", "Poison", "Forest", "Yellow", 2, 0.6, 10.0},
                {"Beedrill", "Bug", "Poison", "Forest", "Yellow", 3, 1.0, 29.5},
                {"Pidgey", "Normal", "Flying", "Forest", "Brown", 1, 0.3, 1.8},
                {"Pidgeotto", "Normal", "Flying", "Forest", "Brown", 2, 1.1, 30.0},
                {"Pidgeot", "Normal", "Flying", "Forest", "Brown", 3, 1.5, 39.5},
                {"Rattata", "Normal", null, "Grassland", "Purple", 1, 0.3, 3.5},
                {"Raticate", "Normal", null, "Grassland", "Brown", 2, 0.7, 18.5},
                {"Spearow", "Normal", "Flying", "Rough Terrain", "Brown", 1, 0.3, 2.0},
                {"Fearow", "Normal", "Flying", "Rough Terrain", "Brown", 2, 1.2, 38.0},
                {"Ekans", "Poison", null, "Grassland", "Purple", 1, 2.0, 6.9},
                {"Arbok", "Poison", null, "Grassland", "Purple", 2, 3.5, 65.0},
                {"Pikachu", "Electric", null, "Forest", "Yellow", 2, 0.4, 6.0},
                {"Raichu", "Electric", null, "Forest", "Yellow", 3, 0.8, 30.0},
                {"Sandshrew", "Ground", null, "Rough Terrain", "Yellow", 1, 0.6, 12.0},
                {"Sandslash", "Ground", null, "Rough Terrain", "Yellow", 2, 1.0, 29.5},
                {"Nidoran♀", "Poison", null, "Grassland", "Blue", 1, 0.4, 7.0},
                {"Nidorina", "Poison", null, "Grassland", "Blue", 2, 0.8, 20.0},
                {"Nidoqueen", "Poison", "Ground", "Grassland", "Blue", 3, 1.3, 60.0},
                {"Nidoran♂", "Poison", null, "Grassland", "Purple", 1, 0.5, 9.0},
                {"Nidorino", "Poison", null, "Grassland", "Purple", 2, 0.9, 19.5},
                {"Nidoking", "Poison", "Ground", "Grassland", "Purple", 3, 1.4, 62.0},
                {"Clefairy", "Fairy", null, "Mountain", "Pink", 2, 0.6, 7.5},
                {"Clefable", "Fairy", null, "Mountain", "Pink", 3, 1.3, 40.0},
                {"Vulpix", "Fire", null, "Grassland", "Brown", 1, 0.6, 9.9},
                {"Ninetales", "Fire", null, "Grassland", "Yellow", 2, 1.1, 19.9},
                {"Jigglypuff", "Normal", "Fairy", "Grassland", "Pink", 2, 0.5, 5.5},
                {"Wigglytuff", "Normal", "Fairy", "Grassland", "Pink", 3, 1.0, 12.0},
                {"Zubat", "Poison", "Flying", "Cave", "Purple", 1, 0.8, 7.5},
                {"Golbat", "Poison", "Flying", "Cave", "Purple", 2, 1.6, 55.0},
                {"Oddish", "Grass", "Poison", "Grassland", "Blue", 1, 0.5, 5.4},
                {"Gloom", "Grass", "Poison", "Grassland", "Blue", 2, 0.8, 8.6},
                {"Vileplume", "Grass", "Poison", "Grassland", "Red", 3, 1.2, 18.6},
                {"Paras", "Bug", "Grass", "Forest", "Red", 1, 0.3, 5.4},
                {"Parasect", "Bug", "Grass", "Forest", "Red", 2, 1.0, 29.5},
                {"Venonat", "Bug", "Poison", "Forest", "Purple", 1, 1.0, 30.0},
                {"Venomoth", "Bug", "Poison", "Forest", "Purple", 2, 1.5, 12.5},
                {"Diglett", "Ground", null, "Cave", "Brown", 1, 0.2, 0.8},
                {"Dugtrio", "Ground", null, "Cave", "Brown", 2, 0.7, 33.3},
                {"Meowth", "Normal", null, "Urban", "Yellow", 1, 0.4, 4.2},
                {"Persian", "Normal", null, "Urban", "Yellow", 2, 1.0, 32.0},
                {"Psyduck", "Water", null, "Water's Edge", "Yellow", 1, 0.8, 19.6},
                {"Golduck", "Water", null, "Water's Edge", "Blue", 2, 1.7, 76.6},
                {"Mankey", "Fighting", null, "Mountain", "Brown", 1, 0.5, 28.0},
                {"Primeape", "Fighting", null, "Mountain", "Brown", 2, 1.0, 32.0},
                {"Growlithe", "Fire", null, "Grassland", "Brown", 1, 0.7, 19.0},
                {"Arcanine", "Fire", null, "Grassland", "Brown", 2, 1.9, 155.0},
                {"Poliwag", "Water", null, "Water's Edge", "Blue", 1, 0.6, 12.4},
                {"Poliwhirl", "Water", null, "Water's Edge", "Blue", 2, 1.0, 20.0},
                {"Poliwrath", "Water", "Fighting", "Water's Edge", "Blue", 3, 1.3, 54.0},
                {"Abra", "Psychic", null, "Urban", "Brown", 1, 0.9, 19.5},
                {"Kadabra", "Psychic", null, "Urban", "Brown", 2, 1.3, 56.5},
                {"Alakazam", "Psychic", null, "Urban", "Brown", 3, 1.5, 48.0},
                {"Machop", "Fighting", null, "Mountain", "Gray", 1, 0.8, 19.5},
                {"Machoke", "Fighting", null, "Mountain", "Gray", 2, 1.5, 70.5},
                {"Machamp", "Fighting", null, "Mountain", "Gray", 3, 1.6, 130.0},
                {"Bellsprout", "Grass", "Poison", "Forest", "Green", 1, 0.7, 4.0},
                {"Weepinbell", "Grass", "Poison", "Forest", "Green", 2, 1.0, 6.4},
                {"Victreebel", "Grass", "Poison", "Forest", "Green", 3, 1.7, 15.5},
                {"Tentacool", "Water", "Poison", "Sea", "Blue", 1, 0.9, 45.5},
                {"Tentacruel", "Water", "Poison", "Sea", "Blue", 2, 1.6, 55.0},
                {"Geodude", "Rock", "Ground", "Mountain", "Brown", 1, 0.4, 20.0},
                {"Graveler", "Rock", "Ground", "Mountain", "Brown", 2, 1.0, 105.0},
                {"Golem", "Rock", "Ground", "Mountain", "Brown", 3, 1.4, 300.0},
                {"Ponyta", "Fire", null, "Grassland", "Yellow", 1, 1.0, 30.0},
                {"Rapidash", "Fire", null, "Grassland", "Yellow", 2, 1.7, 95.0},
                {"Slowpoke", "Water", "Psychic", "Water's Edge", "Pink", 1, 1.2, 36.0},
                {"Slowbro", "Water", "Psychic", "Water's Edge", "Pink", 2, 1.6, 78.5},
                {"Magnemite", "Electric", "Steel", "Rough Terrain", "Gray", 1, 0.3, 6.0},
                {"Magneton", "Electric", "Steel", "Rough Terrain", "Gray", 2, 1.0, 60.0},
                {"Farfetch'd", "Normal", "Flying", "Grassland", "Brown", 1, 0.8, 15.0},
                {"Doduo", "Normal", "Flying", "Grassland", "Brown", 1, 1.4, 39.2},
                {"Dodrio", "Normal", "Flying", "Grassland", "Brown", 2, 1.8, 85.2},
                {"Seel", "Water", null, "Sea", "White", 1, 1.1, 90.0},
                {"Dewgong", "Water", "Ice", "Sea", "White", 2, 1.7, 120.0},
                {"Grimer", "Poison", null, "Urban", "Purple", 1, 0.9, 30.0},
                {"Muk", "Poison", null, "Urban", "Purple", 2, 1.2, 30.0},
                {"Shellder", "Water", null, "Sea", "Purple", 1, 0.3, 4.0},
                {"Cloyster", "Water", "Ice", "Sea", "Purple", 2, 1.5, 132.5},
                {"Gastly", "Ghost", "Poison", "Cave", "Purple", 1, 1.3, 0.1},
                {"Haunter", "Ghost", "Poison", "Cave", "Purple", 2, 1.6, 0.1},
                {"Gengar", "Ghost", "Poison", "Cave", "Purple", 3, 1.5, 40.5},
                {"Onix", "Rock", "Ground", "Cave", "Gray", 1, 8.8, 210.0},
                {"Drowzee", "Psychic", null, "Grassland", "Yellow", 1, 1.0, 32.4},
                {"Hypno", "Psychic", null, "Grassland", "Yellow", 2, 1.6, 75.6},
                {"Krabby", "Water", null, "Water's Edge", "Red", 1, 0.4, 6.5},
                {"Kingler", "Water", null, "Water's Edge", "Red", 2, 1.3, 60.0},
                {"Voltorb", "Electric", null, "Urban", "Red", 1, 0.5, 10.4},
                {"Electrode", "Electric", null, "Urban", "Red", 2, 1.2, 66.6},
                {"Exeggcute", "Grass", "Psychic", "Forest", "Pink", 1, 0.4, 2.5},
                {"Exeggutor", "Grass", "Psychic", "Forest", "Yellow", 2, 2.0, 120.0},
                {"Cubone", "Ground", null, "Mountain", "Brown", 1, 0.4, 6.5},
                {"Marowak", "Ground", null, "Mountain", "Brown", 2, 1.0, 45.0},
                {"Hitmonlee", "Fighting", null, "Urban", "Brown", 2, 1.5, 49.8},
                {"Hitmonchan", "Fighting", null, "Urban", "Brown", 2, 1.4, 50.2},
                {"Lickitung", "Normal", null, "Grassland", "Pink", 1, 1.2, 65.5},
                {"Koffing", "Poison", null, "Urban", "Purple", 1, 0.6, 1.0},
                {"Weezing", "Poison", null, "Urban", "Purple", 2, 1.2, 9.5},
                {"Rhyhorn", "Ground", "Rock", "Rough Terrain", "Gray", 1, 1.0, 115.0},
                {"Rhydon", "Ground", "Rock", "Rough Terrain", "Gray", 2, 1.9, 120.0},
                {"Chansey", "Normal", null, "Urban", "Pink", 2, 1.1, 34.6},
                {"Tangela", "Grass", null, "Grassland", "Blue", 1, 1.0, 35.0},
                {"Kangaskhan", "Normal", null, "Grassland", "Brown", 1, 2.2, 80.0},
                {"Horsea", "Water", null, "Sea", "Blue", 1, 0.4, 8.0},
                {"Seadra", "Water", null, "Sea", "Blue", 2, 1.2, 25.0},
                {"Goldeen", "Water", null, "Water's Edge", "Red", 1, 0.6, 15.0},
                {"Seaking", "Water", null, "Water's Edge", "Red", 2, 1.3, 39.0},
                {"Staryu", "Water", null, "Sea", "Brown", 1, 0.8, 34.5},
                {"Starmie", "Water", "Psychic", "Sea", "Purple", 2, 1.1, 80.0},
                {"Mr. Mime", "Psychic", "Fairy", "Urban", "Pink", 2, 1.3, 54.5},
                {"Scyther", "Bug", "Flying", "Grassland", "Green", 1, 1.5, 56.0},
                {"Jynx", "Ice", "Psychic", "Urban", "Red", 2, 1.4, 40.6},
                {"Electabuzz", "Electric", null, "Grassland", "Yellow", 2, 1.1, 30.0},
                {"Magmar", "Fire", null, "Mountain", "Red", 2, 1.3, 44.5},
                {"Pinsir", "Bug", null, "Forest", "Brown", 1, 1.5, 55.0},
                {"Tauros", "Normal", null, "Grassland", "Brown", 1, 1.4, 88.4},
                {"Magikarp", "Water", null, "Water's Edge", "Red", 1, 0.9, 10.0},
                {"Gyarados", "Water", "Flying", "Water's Edge", "Blue", 2, 6.5, 235.0},
                {"Lapras", "Water", "Ice", "Sea", "Blue", 1, 2.5, 220.0},
                {"Ditto", "Normal", null, "Urban", "Purple", 1, 0.3, 4.0},
                {"Eevee", "Normal", null, "Urban", "Brown", 1, 0.3, 6.5},
                {"Vaporeon", "Water", null, "Urban", "Blue", 2, 1.0, 29.0},
                {"Jolteon", "Electric", null, "Urban", "Yellow", 2, 0.8, 24.5},
                {"Flareon", "Fire", null, "Urban", "Red", 2, 0.9, 25.0},
                {"Porygon", "Normal", null, "Urban", "Pink", 1, 0.8, 36.5},
                {"Omanyte", "Rock", "Water", "Sea", "Blue", 1, 0.4, 7.5},
                {"Omastar", "Rock", "Water", "Sea", "Blue", 2, 1.0, 35.0},
                {"Kabuto", "Rock", "Water", "Sea", "Brown", 1, 0.5, 11.5},
                {"Kabutops", "Rock", "Water", "Sea", "Brown", 2, 1.3, 40.5},
                {"Aerodactyl", "Rock", "Flying", "Mountain", "Purple", 1, 1.8, 59.0},
                {"Snorlax", "Normal", null, "Mountain", "Black", 2, 2.1, 460.0},
                {"Articuno", "Ice", "Flying", "Rare", "Blue", 1, 1.7, 55.4},
                {"Zapdos", "Electric", "Flying", "Rare", "Yellow", 1, 1.6, 52.6},
                {"Moltres", "Fire", "Flying", "Rare", "Yellow", 1, 2.0, 60.0},
                {"Dratini", "Dragon", null, "Water's Edge", "Blue", 1, 1.8, 3.3},
                {"Dragonair", "Dragon", null, "Water's Edge", "Blue", 2, 4.0, 16.5},
                {"Dragonite", "Dragon", "Flying", "Water's Edge", "Brown", 3, 2.2, 210.0},
                {"Mewtwo", "Psychic", null, "Rare", "Purple", 1, 2.0, 122.0},
                {"Mew", "Psychic", null, "Rare", "Pink", 1, 0.4, 4.0},
        };
        PROFILES = new PokemonProfile[rows.length];
        for (int i = 0; i < rows.length; i++) {
            Object[] r = rows[i];
            PROFILES[i] = new PokemonProfile(i + 1, (String) r[0], (String) r[1], (String) r[2], (String) r[3],
                    (String) r[4], (Integer) r[5], (Double) r[6], (Double) r[7]);
        }
    }

    private PokedexData() {
    }

    /** Profile for a Pokedex number (1-151), or null. */
    public static PokemonProfile byNumber(int number) {
        return number >= 1 && number <= PROFILES.length ? PROFILES[number - 1] : null;
    }

    /** Profile for a display name ("Mr. Mime") or PokeAPI name ("mr-mime"), or null. */
    public static PokemonProfile byName(String name) {
        String key = PokeList.toKey(name);
        if (key.isEmpty()) {
            return null;
        }
        for (PokemonProfile profile : PROFILES) {
            if (PokeList.toKey(profile.name).equals(key)) {
                return profile;
            }
        }
        return null;
    }
}
