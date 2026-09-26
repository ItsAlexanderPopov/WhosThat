package com.example.whosthat.league;

import com.example.whosthat.game.Attribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loldle-style facts that Data Dragon doesn't provide, keyed by Data Dragon champion id.
 * Hand-curated from the champions' official bios; champions missing here (e.g. ones released
 * after this table was written) still play, their missing clues just show as "?".
 * Resource and title come from Data Dragon at runtime.
 */
public final class ChampionAttributes {
    public static final class Entry {
        public final String gender;
        public final List<String> positions;
        public final List<String> species;
        public final List<String> rangeTypes;
        public final List<String> regions;
        public final int releaseYear;

        Entry(String gender, String positions, String species, String rangeTypes, String regions, int releaseYear) {
            this.gender = gender;
            this.positions = Attribute.split(positions);
            this.species = Attribute.split(species);
            this.rangeTypes = Attribute.split(rangeTypes);
            this.regions = Attribute.split(regions);
            this.releaseYear = releaseYear;
        }
    }

    private static final String M = "Male";
    private static final String F = "Female";
    private static final String O = "Other";
    private static final String MELEE = "Melee";
    private static final String RANGED = "Ranged";
    private static final String BOTH = "Melee, Ranged";

    private static final Map<String, Entry> ENTRIES = new HashMap<>();

    static {
        // id, gender, positions, species, range, regions, release year
        a("Aatrox", M, "Top", "Darkin", MELEE, "Runeterra", 2013);
        a("Ahri", F, "Middle", "Vastayan", RANGED, "Ionia", 2011);
        a("Akali", F, "Middle, Top", "Human", MELEE, "Ionia", 2010);
        a("Akshan", M, "Middle, Top", "Human", RANGED, "Shurima", 2021);
        a("Alistar", M, "Support", "Minotaur", MELEE, "Runeterra", 2009);
        a("Ambessa", F, "Top", "Human", MELEE, "Noxus", 2024);
        a("Amumu", M, "Jungle, Support", "Yordle, Undead", MELEE, "Shurima", 2009);
        a("Anivia", F, "Middle", "Spirit", RANGED, "Freljord", 2009);
        a("Annie", F, "Middle, Support", "Human, Magicborn", RANGED, "Noxus", 2009);
        a("Aphelios", M, "Bottom", "Human", RANGED, "Targon", 2019);
        a("Ashe", F, "Bottom, Support", "Human, Iceborn", RANGED, "Freljord", 2009);
        a("AurelionSol", M, "Middle", "Celestial, Dragon", RANGED, "Targon", 2016);
        a("Aurora", F, "Middle, Top", "Vastayan", RANGED, "Freljord", 2024);
        a("Azir", M, "Middle", "God-Warrior", RANGED, "Shurima", 2014);
        a("Bard", M, "Support", "Celestial", RANGED, "Runeterra", 2015);
        a("Belveth", F, "Jungle", "Void-Being", MELEE, "Void", 2022);
        a("Blitzcrank", O, "Support", "Golem", MELEE, "Zaun", 2009);
        a("Brand", M, "Support, Middle", "Human, Magically Altered", RANGED, "Freljord", 2011);
        a("Braum", M, "Support", "Human, Iceborn", MELEE, "Freljord", 2014);
        a("Briar", F, "Jungle", "Human, Magically Altered", MELEE, "Noxus", 2023);
        a("Caitlyn", F, "Bottom", "Human", RANGED, "Piltover", 2011);
        a("Camille", F, "Top", "Human, Cyborg", MELEE, "Piltover", 2016);
        a("Cassiopeia", F, "Middle", "Human, Magically Altered", RANGED, "Noxus, Shurima", 2010);
        a("Chogath", M, "Top", "Void-Being", MELEE, "Void", 2009);
        a("Corki", M, "Middle", "Yordle", RANGED, "Bandle City", 2009);
        a("Darius", M, "Top", "Human", MELEE, "Noxus", 2012);
        a("Diana", F, "Jungle, Middle", "Human, Aspect", MELEE, "Targon", 2012);
        a("DrMundo", M, "Top", "Human, Chemically Altered", MELEE, "Zaun", 2009);
        a("Draven", M, "Bottom", "Human", RANGED, "Noxus", 2012);
        a("Ekko", M, "Jungle, Middle", "Human", MELEE, "Zaun", 2015);
        a("Elise", F, "Jungle", "Human, Magically Altered", BOTH, "Noxus", 2012);
        a("Evelynn", F, "Jungle", "Demon", MELEE, "Runeterra", 2009);
        a("Ezreal", M, "Bottom", "Human, Magicborn", RANGED, "Piltover", 2010);
        a("Fiddlesticks", O, "Jungle", "Demon", RANGED, "Runeterra", 2009);
        a("Fiora", F, "Top", "Human", MELEE, "Demacia", 2012);
        a("Fizz", M, "Middle", "Yordle", MELEE, "Bilgewater", 2011);
        a("Galio", M, "Middle, Support", "Golem", MELEE, "Demacia", 2010);
        a("Gangplank", M, "Top", "Human", MELEE, "Bilgewater", 2009);
        a("Garen", M, "Top", "Human", MELEE, "Demacia", 2010);
        a("Gnar", M, "Top", "Yordle", BOTH, "Freljord", 2014);
        a("Gragas", M, "Jungle, Top", "Human", MELEE, "Freljord", 2010);
        a("Graves", M, "Jungle", "Human", RANGED, "Bilgewater", 2011);
        a("Gwen", F, "Top, Jungle", "Magically Altered", MELEE, "Shadow Isles", 2021);
        a("Hecarim", M, "Jungle", "Undead", MELEE, "Shadow Isles", 2012);
        a("Heimerdinger", M, "Middle, Top, Support", "Yordle", RANGED, "Piltover", 2009);
        a("Hwei", M, "Middle, Support", "Human, Magicborn", RANGED, "Ionia", 2023);
        a("Illaoi", F, "Top", "Human", MELEE, "Bilgewater", 2015);
        a("Irelia", F, "Top, Middle", "Human", MELEE, "Ionia", 2010);
        a("Ivern", M, "Jungle", "Human, Magically Altered", RANGED, "Ionia", 2016);
        a("Janna", F, "Support", "Spirit", RANGED, "Zaun", 2009);
        a("JarvanIV", M, "Jungle", "Human", MELEE, "Demacia", 2011);
        a("Jax", M, "Top, Jungle", "Unknown", MELEE, "Runeterra", 2009);
        a("Jayce", M, "Top, Middle", "Human", BOTH, "Piltover", 2012);
        a("Jhin", M, "Bottom", "Human", RANGED, "Ionia", 2016);
        a("Jinx", F, "Bottom", "Human", RANGED, "Zaun", 2013);
        a("Kaisa", F, "Bottom", "Human, Void-Being", RANGED, "Void", 2018);
        a("Kalista", F, "Bottom", "Undead", RANGED, "Shadow Isles", 2014);
        a("Karma", F, "Support, Middle", "Human, Spiritualist", RANGED, "Ionia", 2011);
        a("Karthus", M, "Jungle, Middle", "Undead", RANGED, "Shadow Isles", 2009);
        a("Kassadin", M, "Middle", "Human, Void-Being", MELEE, "Void", 2009);
        a("Katarina", F, "Middle", "Human", MELEE, "Noxus", 2009);
        a("Kayle", F, "Top", "Human, Celestial", BOTH, "Demacia", 2009);
        a("Kayn", M, "Jungle", "Human, Darkin", MELEE, "Ionia", 2017);
        a("Kennen", M, "Top", "Yordle", RANGED, "Ionia", 2010);
        a("Khazix", M, "Jungle", "Void-Being", MELEE, "Void", 2012);
        a("Kindred", O, "Jungle", "God", RANGED, "Runeterra", 2015);
        a("Kled", M, "Top", "Yordle", MELEE, "Noxus", 2016);
        a("KogMaw", M, "Bottom", "Void-Being", RANGED, "Void", 2010);
        a("KSante", M, "Top", "Human", MELEE, "Shurima", 2022);
        a("Leblanc", F, "Middle", "Human, Magicborn", RANGED, "Noxus", 2010);
        a("LeeSin", M, "Jungle", "Human, Spiritualist", MELEE, "Ionia", 2011);
        a("Leona", F, "Support", "Human, Aspect", MELEE, "Targon", 2011);
        a("Lillia", F, "Jungle", "Spirit", RANGED, "Ionia", 2020);
        a("Lissandra", F, "Middle", "Human, Iceborn", RANGED, "Freljord", 2013);
        a("Lucian", M, "Bottom, Middle", "Human", RANGED, "Demacia", 2013);
        a("Lulu", F, "Support", "Yordle", RANGED, "Bandle City", 2012);
        a("Lux", F, "Support, Middle", "Human, Magicborn", RANGED, "Demacia", 2010);
        a("Malphite", M, "Top", "Golem", MELEE, "Ixtal", 2009);
        a("Malzahar", M, "Middle", "Human, Void-Being", RANGED, "Void, Shurima", 2010);
        a("Maokai", M, "Support, Jungle, Top", "Spirit", MELEE, "Shadow Isles", 2011);
        a("MasterYi", M, "Jungle", "Human", MELEE, "Ionia", 2009);
        a("Mel", F, "Middle, Support", "Human, Magicborn", RANGED, "Noxus", 2025);
        a("Milio", M, "Support", "Human, Magicborn", RANGED, "Ixtal", 2023);
        a("MissFortune", F, "Bottom", "Human", RANGED, "Bilgewater", 2010);
        a("MonkeyKing", M, "Top, Jungle", "Vastayan", MELEE, "Ionia", 2011);
        a("Mordekaiser", M, "Top", "Undead", MELEE, "Noxus", 2010);
        a("Morgana", F, "Support, Jungle", "Human, Celestial", RANGED, "Demacia", 2009);
        a("Naafiri", F, "Middle", "Darkin", MELEE, "Shurima", 2023);
        a("Nami", F, "Support", "Vastayan", RANGED, "Runeterra", 2012);
        a("Nasus", M, "Top", "God-Warrior", MELEE, "Shurima", 2009);
        a("Nautilus", M, "Support", "Undead", MELEE, "Bilgewater", 2012);
        a("Neeko", F, "Middle, Support", "Vastayan", RANGED, "Ixtal", 2018);
        a("Nidalee", F, "Jungle", "Human, Spiritualist", BOTH, "Ixtal", 2009);
        a("Nilah", F, "Bottom", "Human", MELEE, "Bilgewater", 2022);
        a("Nocturne", O, "Jungle", "Demon", MELEE, "Runeterra", 2011);
        a("Nunu", M, "Jungle", "Human, Yeti", MELEE, "Freljord", 2009);
        a("Olaf", M, "Top, Jungle", "Human, Iceborn", MELEE, "Freljord", 2010);
        a("Orianna", F, "Middle", "Golem", RANGED, "Piltover", 2011);
        a("Ornn", M, "Top", "God", MELEE, "Freljord", 2017);
        a("Pantheon", M, "Top, Support, Middle", "Human, Aspect", MELEE, "Targon", 2010);
        a("Poppy", F, "Top, Jungle, Support", "Yordle", MELEE, "Demacia", 2010);
        a("Pyke", M, "Support", "Undead", MELEE, "Bilgewater", 2018);
        a("Qiyana", F, "Middle, Jungle", "Human, Magicborn", MELEE, "Ixtal", 2019);
        a("Quinn", F, "Top", "Human", RANGED, "Demacia", 2013);
        a("Rakan", M, "Support", "Vastayan", MELEE, "Ionia", 2017);
        a("Rammus", M, "Jungle", "Unknown", MELEE, "Shurima", 2009);
        a("RekSai", F, "Jungle", "Void-Being", MELEE, "Void", 2014);
        a("Rell", F, "Support", "Human, Magicborn", MELEE, "Noxus", 2020);
        a("Renata", F, "Support", "Human, Chemically Altered", RANGED, "Zaun", 2022);
        a("Renekton", M, "Top", "God-Warrior", MELEE, "Shurima", 2011);
        a("Rengar", M, "Jungle, Top", "Vastayan", MELEE, "Ixtal", 2012);
        a("Riven", F, "Top", "Human", MELEE, "Noxus", 2011);
        a("Rumble", M, "Top", "Yordle", MELEE, "Bandle City", 2011);
        a("Ryze", M, "Middle, Top", "Human, Magicborn", RANGED, "Runeterra", 2009);
        a("Samira", F, "Bottom", "Human", RANGED, "Noxus", 2020);
        a("Sejuani", F, "Jungle", "Human, Iceborn", MELEE, "Freljord", 2012);
        a("Senna", F, "Support, Bottom", "Human", RANGED, "Runeterra", 2019);
        a("Seraphine", F, "Support, Middle, Bottom", "Human, Magicborn", RANGED, "Piltover, Zaun", 2020);
        a("Sett", M, "Top, Support", "Human, Vastayan", MELEE, "Ionia", 2020);
        a("Shaco", M, "Jungle, Support", "Unknown", MELEE, "Runeterra", 2009);
        a("Shen", M, "Top, Support", "Human, Spiritualist", MELEE, "Ionia", 2010);
        a("Shyvana", F, "Jungle", "Human, Dragon", MELEE, "Demacia", 2011);
        a("Singed", M, "Top", "Human, Chemically Altered", MELEE, "Zaun", 2009);
        a("Sion", M, "Top", "Undead", MELEE, "Noxus", 2009);
        a("Sivir", F, "Bottom", "Human", RANGED, "Shurima", 2010);
        a("Skarner", M, "Jungle, Top", "Brackern", MELEE, "Ixtal", 2011);
        a("Smolder", M, "Bottom, Middle", "Dragon", RANGED, "Runeterra", 2024);
        a("Sona", F, "Support", "Human", RANGED, "Demacia", 2010);
        a("Soraka", F, "Support", "Celestial", RANGED, "Targon", 2009);
        a("Swain", M, "Support, Middle", "Human, Magicborn", RANGED, "Noxus", 2010);
        a("Sylas", M, "Middle, Jungle, Top", "Human, Magicborn", MELEE, "Demacia", 2019);
        a("Syndra", F, "Middle", "Human, Magicborn", RANGED, "Ionia", 2012);
        a("TahmKench", M, "Support, Top", "Demon", MELEE, "Bilgewater", 2015);
        a("Taliyah", F, "Jungle, Middle", "Human, Magicborn", RANGED, "Shurima", 2016);
        a("Talon", M, "Middle, Jungle", "Human", MELEE, "Noxus", 2011);
        a("Taric", M, "Support", "Human, Aspect", MELEE, "Targon", 2009);
        a("Teemo", M, "Top", "Yordle", RANGED, "Bandle City", 2009);
        a("Thresh", M, "Support", "Undead", RANGED, "Shadow Isles", 2013);
        a("Tristana", F, "Bottom, Middle", "Yordle", RANGED, "Bandle City", 2009);
        a("Trundle", M, "Jungle, Top", "Troll", MELEE, "Freljord", 2010);
        a("Tryndamere", M, "Top", "Human", MELEE, "Freljord", 2009);
        a("TwistedFate", M, "Middle", "Human, Magicborn", RANGED, "Bilgewater", 2009);
        a("Twitch", M, "Bottom, Jungle", "Chemically Altered", RANGED, "Zaun", 2009);
        a("Udyr", M, "Jungle, Top", "Human, Spiritualist", MELEE, "Freljord", 2009);
        a("Urgot", M, "Top", "Human, Cyborg", RANGED, "Zaun", 2010);
        a("Varus", M, "Bottom, Middle", "Darkin", RANGED, "Ionia", 2012);
        a("Vayne", F, "Bottom, Top", "Human", RANGED, "Demacia", 2011);
        a("Veigar", M, "Middle, Bottom", "Yordle", RANGED, "Bandle City", 2009);
        a("Velkoz", M, "Middle, Support", "Void-Being", RANGED, "Void", 2014);
        a("Vex", F, "Middle", "Yordle", RANGED, "Shadow Isles", 2021);
        a("Vi", F, "Jungle", "Human", MELEE, "Piltover", 2012);
        a("Viego", M, "Jungle", "Undead", MELEE, "Shadow Isles", 2021);
        a("Viktor", M, "Middle", "Human, Cyborg", RANGED, "Zaun", 2011);
        a("Vladimir", M, "Middle, Top", "Human, Magicborn", RANGED, "Noxus", 2010);
        a("Volibear", M, "Top, Jungle", "God", MELEE, "Freljord", 2011);
        a("Warwick", M, "Jungle, Top", "Chemically Altered", MELEE, "Zaun", 2009);
        a("Xayah", F, "Bottom", "Vastayan", RANGED, "Ionia", 2017);
        a("Xerath", M, "Middle, Support", "God-Warrior", RANGED, "Shurima", 2011);
        a("XinZhao", M, "Jungle", "Human", MELEE, "Demacia", 2010);
        a("Yasuo", M, "Middle, Top, Bottom", "Human", MELEE, "Ionia", 2013);
        a("Yone", M, "Middle, Top", "Human, Spirit", MELEE, "Ionia", 2020);
        a("Yorick", M, "Top", "Human", MELEE, "Shadow Isles", 2011);
        a("Yunara", F, "Bottom", "Human", RANGED, "Ionia", 2025);
        a("Yuumi", F, "Support", "Cat", RANGED, "Bandle City", 2019);
        a("Zac", M, "Jungle, Top", "Chemically Altered", MELEE, "Zaun", 2013);
        a("Zed", M, "Middle, Jungle", "Human", MELEE, "Ionia", 2012);
        a("Zeri", F, "Bottom", "Human, Magicborn", RANGED, "Zaun", 2022);
        a("Ziggs", M, "Middle, Bottom", "Yordle", RANGED, "Zaun", 2012);
        a("Zilean", M, "Support, Middle", "Human, Magicborn", RANGED, "Runeterra", 2009);
        a("Zoe", F, "Middle", "Aspect", RANGED, "Targon", 2017);
        a("Zyra", F, "Support", "Spirit", RANGED, "Ixtal", 2012);
    }

    private ChampionAttributes() {
    }

    private static void a(String id, String gender, String positions, String species, String range,
                          String regions, int year) {
        ENTRIES.put(ChampionList.normalizeChampionName(id),
                new Entry(gender, positions, species, range, regions, year));
    }

    /** Facts for a Data Dragon champion id, or null if this table doesn't know the champion. */
    public static Entry get(String championId) {
        return championId != null ? ENTRIES.get(ChampionList.normalizeChampionName(championId)) : null;
    }

    public static int size() {
        return ENTRIES.size();
    }
}
