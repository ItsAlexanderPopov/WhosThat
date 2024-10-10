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

public class PokemonPage extends AppCompatActivity {

    private ImageView imagePokemon;
    private TextInputEditText inputPokemon;
    private Button buttonConfirmPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize UI components
        TextView titlePokemon = findViewById(R.id.title_pokemon);
        imagePokemon = findViewById(R.id.image_pokemon);
        inputPokemon = findViewById(R.id.input_pokemon);
        buttonConfirmPokemon = findViewById(R.id.button_confirm_pokemon);

        // Set up button click listener
        buttonConfirmPokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPokemon();
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

    private void confirmPokemon() {
        // TODO: Implement the logic to check the entered Pokemon name
        String enteredName = inputPokemon.getText().toString().toLowerCase();

    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}