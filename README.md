# WhosThat
Game on Android, guessing a character by known games such as pokemon, league of legends and more

## How to play

Guess the Pokemon (Gen 1 silhouette) or the League of Legends champion (blurred portrait).
Each round you get **6 guesses**. Like Pokedle/Loldle, every wrong guess adds a row of
clues comparing it to the answer:

- green: same value, orange: partial match (e.g. shares one position or type), red: no match
- ↑ / ↓: the answer's number (height, weight, evolution stage, release year) is higher / lower

| Game | Clue columns |
|------|--------------|
| Pokemon | Type 1, Type 2, Habitat, Color, Evolution stage, Height, Weight |
| League of Legends | Gender, Position, Species, Resource, Range, Region, Release year |

**Hints**: tap a hint chip to reveal that fact about the answer (100 pts, or 200 pts for the
champion title / first letter).

**Scoring** for a correct guess: 1000 points, minus 10 per second (at most 500), minus 150 per
wrong guess, minus the hints used, plus 250 for getting it on the first try; never less than 50.
Points add up across rounds, and the run (score and streak) ends when you run out of guesses or
skip. Your best run is saved.

### Data

- Pokemon images come from [PokeAPI](https://pokeapi.co); the clue data in `PokedexData` was
  generated from PokeAPI's data files.
- Champions, portraits, titles and resources come from Riot's Data Dragon (latest patch).
  Gender, position, species, range, region and release year are in `ChampionAttributes`;
  champions missing from that table still play, with those clues shown as "?".
