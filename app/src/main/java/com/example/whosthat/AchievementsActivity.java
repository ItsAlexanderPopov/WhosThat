package com.example.whosthat;

import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AchievementAdapter adapter;
    private List<Achievement> achievements;
    private HighScoreManager highScoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achievement_activity);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Achievements");
        }

        // Set up back navigation
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        highScoreManager = new HighScoreManager(this);
        achievements = createAchievements();
        updateAchievements();

        adapter = new AchievementAdapter(this, achievements);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private List<Achievement> createAchievements() {
        List<Achievement> achievementList = new ArrayList<>();
        achievementList.add(new Achievement("i5lol", "Score 5 streak points in League of Legends", 5, "lol"));
        achievementList.add(new Achievement("i10lol", "Score 10 streak points in League of Legends", 10, "lol"));
        achievementList.add(new Achievement("i15lol", "Score 15 streak points in League of Legends", 15, "lol"));
        achievementList.add(new Achievement("i20lol", "Score 20 streak points in League of Legends", 20, "lol"));
        achievementList.add(new Achievement("i5poke", "Score 5 streak points in Pokemon", 5, "poke"));
        achievementList.add(new Achievement("i10poke", "Score 10 streak points in Pokemon", 10, "poke"));
        achievementList.add(new Achievement("i15poke", "Score 15 streak points in Pokemon", 15, "poke"));
        achievementList.add(new Achievement("i20poke", "Score 20 streak points in Pokemon", 20, "poke"));
        achievementList.add(new Achievement("secret", "Secret Achievement", 1, "secret"));
        return achievementList;
    }

    private void updateAchievements() {
        int pokemonScore = highScoreManager.getHighScorePoke();
        int lolScore = highScoreManager.getHighScoreLeagueOfLegends();

        for (Achievement achievement : achievements) {
            if (achievement.getGameType().equals("poke")) {
                achievement.setUnlocked(pokemonScore >= achievement.getRequiredScore());
            } else if (achievement.getGameType().equals("lol")) {
                achievement.setUnlocked(lolScore >= achievement.getRequiredScore());
            }
        }
    }
}