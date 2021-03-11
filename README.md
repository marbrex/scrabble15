# Scrabble game project [Group 15]
This is a team project of **Software Engineering Practice** in **Mannheim University**.

# Game rules
A player makes a play by putting a valid word on the board. The first word has to be played through the middle of the board (star) and it must be at least two letters long. After that a valid move is made by using one or more tiles to place a word on the board. This new word may use an already existing word or must join with the cluster of tiles already on the board.

Each turn a player has 3 options:
- pass
- exchange one or more tiles for an equal number from the bag, scoring nothing. (at least 7 tiles must remain in the bag after the exchange)
- play at least on tile on the board, adding the value of all words formed to the player’s cumulative score.

A word can only be played as a continuous string of letters reading from left to right or top to bottom. The main word must either use the letters of one or more previously played words or else have at least one of its tiles horizontally or vertically adjacent to an already played word. If any words other than the main word are formed by the play, they are scored as well and are subject to the same criteria of acceptability.

If a blank tile (joker) is played the player has to define for what letter the blank tile stands for and it remains to stand for this letter throughout the game. The blank tile scores zero points.

After making a play, the player announces the score for that play and draws tiles from the bag to replenish their rack to seven tiles. If there are not enough tiles in the bag to do so, the player takes all the remaining tiles.

The game is ended by any of these causes:
- One player plays every tile on their rack, and there are no tiles remaining in the bag (regardless of the tiles on the opponent's rack).
- At least six successive scoreless turns have occurred and either player decides to end the game.
- A player uses more than 10 minutes of overtime.

# Maintainers
- Eldar Kasmamytov [*@ekasmamy*]
- Alexander Starchenkov [*@astarche*]
- Hendrik Donatus Diehl [*@hendiehl*]
- Moritz Raucher [*@mraucher*]
- Sergen Keskincelik [*@skeskinc*]