package com.example.whosthat.game;

/** How one attribute of a guess compares to the answer, like the tile colors in Pokedle/Loldle. */
public enum Match {
    CORRECT,
    PARTIAL,
    WRONG,
    UNKNOWN
}
