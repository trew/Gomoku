Gomoku Multiplayer
===

A multiplayer game of Gomoku.

---

## Current features:
1. Configurable board size: 5x5 - 40x40
2. 3&3 rule, ban moves that gives you two open rows of 3 stones
3. 4&4 rule, ban moves that gives you two rops of 4 stones (open or not)
4. Overlines, whether rows of 6 or more counts
5. Headless server with ability to serve multiple games
6. Clients can create its own server and host a single game
7. Clients can join as spectators

---

### Planned features:
1. Larger configurable board size.
2. Allow various opening rules
   1. Pro
      * Blacks 3rd move must be outside a 5x5 area from the middle
   2. Long Pro
      * Blacks 3rd move must be outside a 7x7 area from the middle
   3. Swap
      * The starting player chooses the first three stones (black, then white, then black again). The second player may then choose which color to play with.
   4. Swap2 
      * The starting player chooses the first three stones (black, then white, then black again). The second player now has three options:
         1. Play as white
         2. Play as black
         3. Put down two more stones (white, then black) and let the other play choose color.
