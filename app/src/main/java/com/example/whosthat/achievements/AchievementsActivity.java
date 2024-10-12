package com.example.whosthat.achievements;

import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whosthat.HighScoreManager;
import com.example.whosthat.R;

import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AchievementAdapter adapter;
    private List<AchievementModel> achievementModels;
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
        achievementModels = createAchievements();
        updateAchievements();

        adapter = new AchievementAdapter(this, achievementModels);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private List<AchievementModel> createAchievements() {
        List<AchievementModel> achievementModelList = new ArrayList<>();
        achievementModelList.add(new AchievementModel("i5lol", "Score 5 streak points in League of Legends", 5, "lol"));
        achievementModelList.add(new AchievementModel("i10lol", "Score 10 streak points in League of Legends", 10, "lol"));
        achievementModelList.add(new AchievementModel("i15lol", "Score 15 streak points in League of Legends", 15, "lol"));
        achievementModelList.add(new AchievementModel("i20lol", "Score 20 streak points in League of Legends", 20, "lol"));
        achievementModelList.add(new AchievementModel("i5poke", "Score 5 streak points in Pokemon", 5, "poke"));
        achievementModelList.add(new AchievementModel("i10poke", "Score 10 streak points in Pokemon", 10, "poke"));
        achievementModelList.add(new AchievementModel("i15poke", "Score 15 streak points in Pokemon", 15, "poke"));
        achievementModelList.add(new AchievementModel("i20poke", "Score 20 streak points in Pokemon", 20, "poke"));
        // Add the secret achievement
        achievementModelList.add(new AchievementModel("secret", "Secret Achievement", 0, "secret"));
        return achievementModelList;
    }

    private void updateAchievements() {
        int pokemonScore = highScoreManager.getHighStreakPokemon();
        int lolScore = highScoreManager.getHighStreakLeagueOfLegends();
        boolean isSecretUnlocked = highScoreManager.isSecretAchievementUnlocked();

        for (AchievementModel achievementModel : achievementModels) {
            switch (achievementModel.getGameType()) {
                case "poke":
                    achievementModel.setUnlocked(pokemonScore >= achievementModel.getRequiredScore());
                    break;
                case "lol":
                    achievementModel.setUnlocked(lolScore >= achievementModel.getRequiredScore());
                    break;
                case "secret":
                    achievementModel.setUnlocked(isSecretUnlocked);
                    if (isSecretUnlocked) {
                        achievementModel.setImageResource("next.png");
                        achievementModel.setDescription("Discover secret keyword of \"next\"");
                    } else {
                        achievementModel.setImageResource("secret.png");
                    }
                    break;
            }
        }
    }
}