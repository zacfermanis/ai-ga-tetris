ai-ga-tetris
============

A Genetic Algorithm trained to play Tetris

##Setup

Maven + Spring Boot, typical setup. Nothing fancy.

Launch AITetrisApplication with no profiles/args.


## Game Modes 

There are 3 ways to run (driven by application.yml entries)
1. Play as human (set use_ai = false)
2. Watch AI Play (set use_ai = true, train_ai = false)
3. Train AI (set use_ai=true, train_ai = false);

Coming soon:
Record/Clone yourself as AI  - Will record your games and calculate weights to simulate your play style as an AI. Potential to play against your own AI.