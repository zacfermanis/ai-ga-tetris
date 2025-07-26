# AI Tetris Project - Development Guide

## Project Overview
This is a Java Spring Boot application that implements Tetris with a genetic algorithm AI player. The project uses Maven for build management and includes sound management, game mechanics, and AI optimization.

## Code Style & Standards
- Follow Java naming conventions (camelCase for variables/methods, PascalCase for classes)
- Use meaningful variable and method names
- Add comments for complex algorithms, especially in genetic algorithm logic
- Keep methods focused and single-purpose
- Use proper indentation (4 spaces)

## Architecture Guidelines
- Maintain separation between game logic (TetrisEngine), AI logic (GeneticAIAlgorithm), and UI (GameWindow, TetrisPanel)
- Use Spring Boot annotations appropriately (@SpringBootApplication, @Component, etc.)
- Keep sound management separate (SoundManager)
- Preserve the genetic algorithm structure for AI optimization

## File Organization
- Main application: `AiTetrisApplication.java`
- Game core: `TetrisEngine.java`, `Tetromino.java`, `Block.java`
- AI components: `GeneticAIAlgorithm.java`, `TetrisAI.java`
- UI components: `GameWindow.java`, `TetrisPanel.java`
- Utilities: `SoundManager.java`, `ProjectConstants.java`

## Build & Run
- Use Maven wrapper: `./mvnw clean install` to build
- Run with: `./mvnw spring-boot:run`
- Ensure Java 21+ is available

## Testing
- Maintain existing test structure in `src/test/java`
- Add tests for new AI algorithms or game mechanics
- Use Spring Boot test annotations for integration tests

## Dependencies
- Spring Boot 2.1.4.RELEASE
- Java 21+
- Maven for build management
- Sound files in `src/main/resources/sound/`
- Image resources in `src/main/resources/image/`

## AI/Genetic Algorithm Notes
- Preserve the genetic algorithm implementation in `GeneticAIAlgorithm.java`
- Maintain fitness function logic for Tetris evaluation
- Keep generation data in `src/main/resources/generations/`
- Document any changes to AI parameters or algorithms

## Performance Considerations
- Optimize game loop performance
- Ensure AI calculations don't block UI
- Consider memory usage for genetic algorithm populations
- Profile if performance issues arise 

