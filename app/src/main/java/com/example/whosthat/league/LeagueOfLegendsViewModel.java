package com.example.whosthat.league;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.whosthat.GameRepository;
import com.example.whosthat.game.Attribute;
import com.example.whosthat.game.GuessGameViewModel;
import com.example.whosthat.game.GuessOutcome;
import com.example.whosthat.game.Hint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class LeagueOfLegendsViewModel extends GuessGameViewModel<ChampionProfile> {
    private static final String TAG = "LeagueViewModel";
    private static final int INITIAL_BLUR_RADIUS = 70;
    private static final int BLUR_REDUCTION_STEP = 12;
    private static final int MIN_BLUR_RADIUS = 1;
    private static final int ATTRIBUTE_HINT_COST = 100;
    private static final int TEXT_HINT_COST = 200;

    // Same columns as Loldle's classic mode
    static final List<Attribute<ChampionProfile>> ATTRIBUTES = Collections.unmodifiableList(Arrays.asList(
            Attribute.<ChampionProfile>text("Gender", c -> c.gender),
            Attribute.<ChampionProfile>set("Position", c -> c.positions),
            Attribute.<ChampionProfile>set("Species", c -> c.species),
            Attribute.<ChampionProfile>text("Resource", c -> c.resource),
            Attribute.<ChampionProfile>set("Range", c -> c.rangeTypes),
            Attribute.<ChampionProfile>set("Region", c -> c.regions),
            Attribute.<ChampionProfile>number("Released", c -> c.releaseYear != null ? c.releaseYear.doubleValue() : null,
                    v -> String.format(Locale.US, "%d", v.intValue()))
    ));

    static final List<Hint<ChampionProfile>> HINTS;

    static {
        List<Hint<ChampionProfile>> hints = new ArrayList<>();
        for (Attribute<ChampionProfile> attribute : ATTRIBUTES) {
            hints.add(Hint.of(attribute, ATTRIBUTE_HINT_COST));
        }
        hints.add(new Hint<>("Title", TEXT_HINT_COST, c -> c.title));
        hints.add(new Hint<>("First letter", TEXT_HINT_COST, c -> c.name.substring(0, 1)));
        HINTS = Collections.unmodifiableList(hints);
    }

    private final MutableLiveData<Integer> currentBlurRadius = new MutableLiveData<>(INITIAL_BLUR_RADIUS);
    private final MutableLiveData<Boolean> isChampionListLoaded = new MutableLiveData<>(false);

    private final LeagueApiService leagueApiService;
    private final GameRepository gameRepository;
    private final Random random = new Random();

    public LeagueOfLegendsViewModel(LeagueApiService leagueApiService) {
        this.leagueApiService = leagueApiService;
        this.gameRepository = GameRepository.getInstance();
    }

    public LiveData<String> getCurrentChampionName() { return gameRepository.getCurrentChampionName(); }
    public LiveData<String> getCurrentChampionPortraitUrl() { return gameRepository.getCurrentChampionPortraitUrl(); }
    public LiveData<Integer> getCurrentBlurRadius() { return currentBlurRadius; }
    public LiveData<Boolean> getIsChampionListLoaded() { return isChampionListLoaded; }

    @Override
    protected List<Attribute<ChampionProfile>> attributes() {
        return ATTRIBUTES;
    }

    @Override
    protected List<Hint<ChampionProfile>> hints() {
        return HINTS;
    }

    @Override
    protected ChampionProfile findSubject(String input) {
        LeagueChampionModel.ChampionData data = ChampionList.findChampion(input);
        return data != null ? ChampionProfile.from(data) : null;
    }

    @Override
    protected String nameOf(ChampionProfile subject) {
        return subject.name;
    }

    public void loadChampionList() {
        if (ChampionList.isInitialized()) {
            isChampionListLoaded.setValue(true);
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);
        ChampionListLoader.load(leagueApiService, new ChampionListLoader.Listener() {
            @Override
            public void onLoaded() {
                isLoading.setValue(false);
                isChampionListLoaded.setValue(true);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void fetchRandomChampion() {
        LeagueChampionModel.ChampionData champion =
                ChampionList.getRandomChampion(random, gameRepository.getCurrentChampionName().getValue());
        if (champion == null) {
            Log.e(TAG, "Champion list not initialized. Cannot pick a random champion.");
            errorMessage.setValue("Champion list not loaded yet");
            return;
        }

        String portraitUrl = ChampionList.getPortraitUrl(champion);
        Log.d(TAG, "Picked " + champion.getName() + " -> " + portraitUrl);
        // Reset the blur before publishing the new image so it is never shown unblurred
        currentBlurRadius.setValue(INITIAL_BLUR_RADIUS);
        gameRepository.setCurrentChampionName(champion.getName());
        gameRepository.setCurrentChampionPortraitUrl(portraitUrl);
    }

    /** Called once the current champion's portrait is on screen; starts its round (and clock). */
    public void onChampionShown() {
        ChampionProfile target = findSubject(gameRepository.getCurrentChampionName().getValue());
        if (target == null) {
            Log.e(TAG, "Unknown champion " + gameRepository.getCurrentChampionName().getValue());
            return;
        }
        startRoundIfNew(target);
    }

    @Override
    public GuessOutcome submitGuess(String input) {
        GuessOutcome outcome = super.submitGuess(input);
        if (outcome.type == GuessOutcome.Type.WRONG) {
            reduceBlurRadius();
        }
        return outcome;
    }

    private void reduceBlurRadius() {
        int currentRadius = currentBlurRadius.getValue() != null ? currentBlurRadius.getValue() : INITIAL_BLUR_RADIUS;
        int newRadius = Math.max(MIN_BLUR_RADIUS, currentRadius - BLUR_REDUCTION_STEP);
        currentBlurRadius.setValue(newRadius);
    }
}
