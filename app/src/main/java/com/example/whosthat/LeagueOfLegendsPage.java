package com.example.whosthat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

public class LeagueOfLegendsPage extends AppCompatActivity {

    private ImageView imageChampion;
    private TextInputEditText inputChampion;
    private Button buttonConfirmChampion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leagueoflegends);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize UI components
        TextView titleChampion = findViewById(R.id.title_champion);
        imageChampion = findViewById(R.id.image_champion);
        inputChampion = findViewById(R.id.input_champion);
        buttonConfirmChampion = findViewById(R.id.button_confirm_champion);

        // Set up button click listener
        buttonConfirmChampion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmChampion();
            }
        });

        // Handle back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void confirmChampion() {
        // TODO: Implement the logic to check the entered Champion name
        String enteredName = inputChampion.getText().toString();
        // Add your game logic here
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}