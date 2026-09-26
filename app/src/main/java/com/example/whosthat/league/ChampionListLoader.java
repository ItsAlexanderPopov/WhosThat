package com.example.whosthat.league;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Loads the champion list from Data Dragon once, using the newest patch version.
 * Concurrent callers (e.g. the main menu preload and the game screen) share one request.
 */
public final class ChampionListLoader {
    private static final String TAG = "ChampionListLoader";
    // Used only if versions.json can't be fetched
    static final String FALLBACK_VERSION = "16.11.1";

    public interface Listener {
        void onLoaded();

        void onError(String message);
    }

    private static final List<Listener> pending = new ArrayList<>();
    private static boolean inFlight = false;

    private ChampionListLoader() {
    }

    /** Must be called on the main thread; listeners are notified on the main thread. */
    public static void load(LeagueApiService api, Listener listener) {
        if (ChampionList.isInitialized()) {
            if (listener != null) listener.onLoaded();
            return;
        }
        if (listener != null) pending.add(listener);
        if (inFlight) {
            return;
        }
        inFlight = true;

        api.getVersions().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                String version = FALLBACK_VERSION;
                List<String> versions = response.body();
                if (response.isSuccessful() && versions != null && !versions.isEmpty()) {
                    version = versions.get(0);
                } else {
                    Log.w(TAG, "Couldn't read versions.json (" + response.code() + "), using " + FALLBACK_VERSION);
                }
                loadChampions(api, version);
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.w(TAG, "Couldn't fetch versions.json, using " + FALLBACK_VERSION, t);
                loadChampions(api, FALLBACK_VERSION);
            }
        });
    }

    private static void loadChampions(LeagueApiService api, String version) {
        Log.d(TAG, "Loading champion list for patch " + version);
        api.getChampionList(version).enqueue(new Callback<LeagueChampionModel.ChampionList>() {
            @Override
            public void onResponse(Call<LeagueChampionModel.ChampionList> call, Response<LeagueChampionModel.ChampionList> response) {
                LeagueChampionModel.ChampionList body = response.body();
                Map<String, LeagueChampionModel.ChampionData> data = body != null ? body.getChampions() : null;
                if (response.isSuccessful() && data != null && !data.isEmpty()) {
                    ChampionList.initialize(body.getVersion() != null ? body.getVersion() : version, data.values());
                    Log.d(TAG, "Loaded " + data.size() + " champions");
                    finish(null);
                } else {
                    finish("Error loading champion list: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LeagueChampionModel.ChampionList> call, Throwable t) {
                Log.e(TAG, "Network error while loading champion list", t);
                finish("Network error: " + t.getMessage());
            }
        });
    }

    private static void finish(String error) {
        inFlight = false;
        List<Listener> listeners = new ArrayList<>(pending);
        pending.clear();
        for (Listener listener : listeners) {
            if (error == null) {
                listener.onLoaded();
            } else {
                listener.onError(error);
            }
        }
    }
}
