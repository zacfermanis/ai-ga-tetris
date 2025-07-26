# Project Brief: AI-GA-Tetris

## Project Overview
AI-GA-Tetris is a Java-based Tetris game implementation featuring a Genetic Algorithm-powered AI that learns to play optimally through evolutionary computation. The project demonstrates artificial intelligence concepts through a classic game environment.

## Core Requirements

### Primary Goals
1. **Implement Classic Tetris Gameplay**: Full implementation of standard Tetris mechanics including piece movement, rotation, line clearing, and scoring
2. **Develop Genetic Algorithm AI**: Create a self-learning AI system that evolves through generations to improve gameplay performance
3. **Provide Multiple Game Modes**: Support human play, AI watch mode, and AI training mode
4. **Enable Experimentation**: Allow configuration of genetic algorithm parameters for research and optimization

### Technical Requirements
- **Platform**: Java 8+ with Spring Boot framework
- **UI**: Java Swing for graphical interface
- **Build System**: Maven for dependency management and build automation
- **Audio Support**: Optional sound effects and background music
- **Data Persistence**: Save/load AI generations for continued training

### Functional Requirements
- **Game Mechanics**: Complete Tetris gameplay with all standard rules
- **AI Decision Making**: Real-time evaluation of board states and piece placement
- **Genetic Evolution**: Population-based optimization with crossover, mutation, and selection
- **Configuration Management**: YAML-based configuration for all game and AI parameters
- **Performance Monitoring**: Track AI performance across generations

## Success Criteria
- AI demonstrates measurable improvement over generations
- Game runs smoothly with consistent frame rates
- All game modes function correctly
- Configuration system allows easy parameter adjustment
- Codebase is maintainable and well-documented

## Project Scope
- **In Scope**: Core Tetris game, genetic algorithm AI, basic UI, sound system, configuration management
- **Out of Scope**: Multiplayer functionality, advanced graphics, web interface, mobile support

## Key Stakeholders
- **Primary User**: AI/ML researchers and enthusiasts interested in genetic algorithms
- **Secondary Users**: Tetris players wanting to watch AI play, developers learning about evolutionary computation 