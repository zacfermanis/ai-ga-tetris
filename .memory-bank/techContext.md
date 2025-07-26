# Technical Context: AI-GA-Tetris

## Technology Stack

### Core Technologies
- **Language**: Java 8+ (Spring Boot 2.1.4.RELEASE)
- **Framework**: Spring Boot for application structure and configuration
- **UI Framework**: Java Swing for graphical interface
- **Build Tool**: Maven for dependency management and build automation
- **Configuration**: YAML-based configuration files

### Development Tools
- **IDE**: Compatible with IntelliJ IDEA, Eclipse, VS Code
- **Version Control**: Git
- **Package Manager**: Maven (via `pom.xml`)
- **Testing**: JUnit (Spring Boot Test framework)

## Development Setup

### Prerequisites
```bash
# Required Software
- Java 8 or higher (JDK)
- Maven 3.3 or higher
- Git (for version control)
```

### Environment Setup
```bash
# Clone repository
git clone https://github.com/yourusername/ai-ga-tetris.git
cd ai-ga-tetris

# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

### Project Structure
```
ai-ga-tetris/
├── src/
│   ├── main/
│   │   ├── java/com/fermanis/aitetris/
│   │   │   ├── AiTetrisApplication.java      # Main entry point
│   │   │   ├── GameWindow.java              # Main UI window
│   │   │   ├── TetrisPanel.java             # Game rendering
│   │   │   ├── TetrisEngine.java            # Core game logic
│   │   │   ├── TetrisAI.java                # AI decision making
│   │   │   ├── GeneticAIAlgorithm.java      # Genetic algorithm
│   │   │   ├── Tetromino.java               # Piece definitions
│   │   │   ├── Block.java                   # Block representation
│   │   │   ├── SoundManager.java            # Audio management
│   │   │   └── ProjectConstants.java        # Constants
│   │   └── resources/
│   │       ├── application.yml              # Configuration
│   │       ├── generations/                 # AI generation data
│   │       ├── image/                       # Graphics assets
│   │       └── sound/                       # Audio files
│   └── test/
│       └── java/com/fermanis/aitetris/
│           └── AiTetrisApplicationTests.java
├── pom.xml                                  # Maven configuration
├── README.md                                # Project documentation
└── .memory-bank/                            # Memory bank files
```

## Dependencies

### Core Dependencies (from pom.xml)
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Build Configuration
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

## Configuration Management

### Application Configuration (`application.yml`)
```yaml
app:
  use_sounds: false      # Enable/disable sound effects
  use_ai: true          # Enable AI control
  train_ai: true        # Enable AI training mode

genetic_algo:
  population: 4                    # Population size (must be multiple of 4)
  mutuation_rate: 0.05            # Mutation probability
  runs_per_eval: 3                # Games per evaluation
  serialize_generation: true      # Save generations to disk
  use_loadedGeneration: false     # Load previous generation
  use_preset_population: true     # Use preset starting population
  reproduction:
    useTopHalf: true              # Select top 50% for breeding
    useCrossover: true            # Use crossover reproduction
    useParentsAverage: false      # Average parent weights
    useTwinPrevention: true       # Prevent identical offspring
```

### Configuration Injection
- Spring Boot `@Value` annotations for parameter injection
- Static configuration variables in `AiTetrisApplication`
- Runtime configuration through YAML file

## Technical Constraints

### Platform Constraints
- **Java Version**: Minimum Java 8, tested with Java 21
- **Operating System**: Cross-platform (Windows, macOS, Linux)
- **Memory**: Sufficient RAM for genetic algorithm populations
- **Graphics**: Java Swing requires display capability

### Performance Constraints
- **Frame Rate**: Target 60 FPS for smooth gameplay
- **AI Response Time**: AI decisions must complete within frame budget
- **Memory Usage**: Efficient management of generation data
- **CPU Usage**: Genetic algorithm computations should not block UI

### Audio Constraints
- **File Formats**: WAV for sound effects, MIDI for background music
- **Resource Loading**: Audio files loaded from classpath resources
- **Playback**: Java Sound API for audio output

## Development Workflow

### Build Process
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn package

# Run with Spring Boot
mvn spring-boot:run
```

### Testing Strategy
- **Unit Tests**: Individual component testing
- **Integration Tests**: Spring Boot application context testing
- **Manual Testing**: Gameplay and AI behavior verification

### Code Quality
- **Naming Conventions**: Java camelCase for variables/methods, PascalCase for classes
- **Documentation**: Inline comments for complex algorithms
- **Error Handling**: Comprehensive exception handling with user-friendly messages

## Deployment Considerations

### JAR Distribution
- Spring Boot creates executable JAR with embedded dependencies
- Single file deployment simplifies distribution
- Configuration external to JAR for easy customization

### Resource Management
- Audio and image files embedded in JAR
- Generation data stored in external files
- Configuration files can be externalized

## Future Technical Considerations

### Potential Enhancements
- **Parallel Processing**: Multi-threaded genetic algorithm evaluation
- **Web Interface**: Browser-based version using Spring Web
- **Database Integration**: Persistent storage for AI generations
- **Performance Profiling**: Built-in performance monitoring
- **Plugin Architecture**: Extensible AI algorithm system 