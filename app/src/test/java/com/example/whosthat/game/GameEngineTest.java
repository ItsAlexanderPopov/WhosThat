package com.example.whosthat.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.whosthat.league.ChampionAttributes;
import com.example.whosthat.league.ChampionList;
import com.example.whosthat.league.ChampionProfile;
import com.example.whosthat.league.LeagueChampionModel;
import com.example.whosthat.pokemon.PokeList;
import com.example.whosthat.pokemon.PokedexData;
import com.example.whosthat.pokemon.PokemonProfile;
import com.google.gson.Gson;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameEngineTest {
    private static final Attribute<PokemonProfile> TYPE1 = Attribute.<PokemonProfile>text("Type 1", p -> p.type1)
            .partialIfIn(p -> p.type2 != null ? Collections.singletonList(p.type2) : null);
    private static final Attribute<PokemonProfile> HEIGHT = Attribute.number("Height", p -> p.heightM, v -> v + " m");

    @Test
    public void scoring_firstTryFastGetsBonus() {
        Scoring.Breakdown b = Scoring.calculate(5_000, 0, 0);
        assertEquals(1000 - 50 + Scoring.FIRST_TRY_BONUS, b.total);
        assertEquals(1, b.guessNumber);
    }

    @Test
    public void scoring_deductsTimeWrongGuessesAndHints() {
        Scoring.Breakdown b = Scoring.calculate(12_400, 2, 300);
        assertEquals(1000 - 120 - 300 - 300, b.total);
        assertEquals(3, b.guessNumber);
    }

    @Test
    public void scoring_timePenaltyIsCappedAndPointsHaveAFloor() {
        assertEquals(Scoring.MAX_TIME_PENALTY, Scoring.calculate(600_000, 1, 0).timePenalty);
        assertEquals(Scoring.MIN_POINTS, Scoring.calculate(600_000, 5, 800).total);
    }

    @Test
    public void pokedex_hasAll151InListOrder() {
        List<String> names = PokeList.getGen1PokemonList();
        assertEquals(151, names.size());
        for (int i = 0; i < names.size(); i++) {
            assertEquals(names.get(i), PokedexData.byNumber(i + 1).name);
        }
        assertEquals("Mr. Mime", PokedexData.byName("mr-mime").name);
        assertEquals("Nidoran♀", PokedexData.byName("nidoran-f").name);
        assertNull(PokedexData.byName("togepi"));
    }

    @Test
    public void pokemonClues_matchPokedleRules() {
        PokemonProfile venusaur = PokedexData.byName("Venusaur");   // Grass/Poison
        PokemonProfile ekans = PokedexData.byName("Ekans");         // Poison
        PokemonProfile oddish = PokedexData.byName("Oddish");       // Grass/Poison
        PokemonProfile charmander = PokedexData.byName("Charmander");

        assertEquals(Match.PARTIAL, TYPE1.compare(ekans, venusaur).match);
        assertEquals(Match.CORRECT, TYPE1.compare(oddish, venusaur).match);
        assertEquals(Match.WRONG, TYPE1.compare(charmander, venusaur).match);

        Clue height = HEIGHT.compare(oddish, venusaur);
        assertEquals(Match.WRONG, height.match);
        assertEquals(Clue.ARROW_HIGHER, height.arrow);
        assertEquals(Clue.ARROW_LOWER, HEIGHT.compare(venusaur, oddish).arrow);
    }

    @Test
    public void setAttribute_partialOnOverlap() {
        Attribute<List<String>> positions = Attribute.set("Position", l -> l);
        assertEquals(Match.CORRECT, positions.compare(Arrays.asList("Jungle", "Top"), Arrays.asList("top", "jungle")).match);
        assertEquals(Match.PARTIAL, positions.compare(Arrays.asList("Top"), Arrays.asList("Top", "Jungle")).match);
        assertEquals(Match.WRONG, positions.compare(Arrays.asList("Bottom"), Arrays.asList("Top")).match);
        assertEquals(Match.UNKNOWN, positions.compare(null, Arrays.asList("Top")).match);
    }

    @Test
    public void round_tracksGuessesHintsAndClock() {
        PokemonProfile target = PokedexData.byName("Pikachu");
        Hint<PokemonProfile> hint = Hint.of(TYPE1, 100);
        Round<PokemonProfile> round = new Round<>(target, target.name,
                Arrays.asList(TYPE1, HEIGHT), Collections.singletonList(hint));

        round.resume(0);
        round.pause(4_000);          // time away from the screen doesn't count
        round.resume(60_000);
        assertEquals(6_000, round.elapsedMs(62_000));

        round.addGuess("Raichu", PokedexData.byName("Raichu"), false, 62_000);
        assertEquals("Electric", round.revealHint(0));
        assertNull(round.revealHint(0));  // not charged twice
        assertEquals(100, round.hintCost());
        assertTrue(round.hasGuessed("raichu"));

        round.addGuess(target.name, target, true, 64_000);
        assertEquals(Round.State.WON, round.getState());
        assertEquals(1000 - 80 - 150 - 100, round.getResult().total);
        assertEquals(2, round.getResult().guessNumber);
    }

    @Test
    public void round_isLostAfterMaxGuesses() {
        PokemonProfile target = PokedexData.byNumber(1);
        Round<PokemonProfile> round = new Round<>(target, target.name,
                Collections.singletonList(TYPE1), Collections.<Hint<PokemonProfile>>emptyList());
        for (int i = 0; i < Round.MAX_GUESSES; i++) {
            PokemonProfile guess = PokedexData.byNumber(i + 2);
            round.addGuess(guess.name, guess, false, 0);
        }
        assertEquals(Round.State.LOST, round.getState());
        assertEquals(0, round.guessesLeft());
    }

    @Test
    public void champions_mergeDataDragonWithAttributeTable() {
        String json = "{\"version\":\"16.11.1\",\"data\":{"
                + "\"MonkeyKing\":{\"id\":\"MonkeyKing\",\"name\":\"Wukong\",\"title\":\"the Monkey King\","
                + "\"partype\":\"Mana\",\"image\":{\"full\":\"MonkeyKing.png\"},\"stats\":{\"attackrange\":175}},"
                + "\"Garen\":{\"id\":\"Garen\",\"name\":\"Garen\",\"title\":\"The Might of Demacia\","
                + "\"partype\":\"None\",\"image\":{\"full\":\"Garen.png\"},\"stats\":{\"attackrange\":175}},"
                + "\"Newchamp\":{\"id\":\"Newchamp\",\"name\":\"Newchamp\",\"title\":\"the Unknown\","
                + "\"partype\":\"Energy\",\"image\":{\"full\":\"Newchamp.png\"},\"stats\":{\"attackrange\":550}}}}";
        LeagueChampionModel.ChampionList list = new Gson().fromJson(json, LeagueChampionModel.ChampionList.class);
        ChampionList.initialize(list.getVersion(), list.getChampions().values());

        ChampionProfile wukong = ChampionProfile.from(ChampionList.findChampion("wukong"));
        assertEquals("Male", wukong.gender);
        assertEquals(Arrays.asList("Ionia"), wukong.regions);
        assertEquals(Integer.valueOf(2011), wukong.releaseYear);
        assertEquals("the Monkey King", wukong.title);

        ChampionProfile garen = ChampionProfile.from(ChampionList.findChampion("Garen"));
        assertEquals("Manaless", garen.resource);

        // Not in the table: runtime data still works, the rest is unknown
        ChampionProfile unknown = ChampionProfile.from(ChampionList.findChampion("Newchamp"));
        assertNull(unknown.gender);
        assertEquals(Arrays.asList("Ranged"), unknown.rangeTypes);
        assertEquals("Energy", unknown.resource);
    }

    @Test
    public void championTable_isComplete() {
        assertTrue(ChampionAttributes.size() >= 170);
        assertNotNull(ChampionAttributes.get("MonkeyKing"));
        assertNotNull(ChampionAttributes.get("Nunu"));
        assertFalse(ChampionAttributes.get("Kayle").rangeTypes.size() < 2);
    }
}
