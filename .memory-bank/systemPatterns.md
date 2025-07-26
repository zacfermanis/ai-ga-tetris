# System Patterns: AI-GA-Tetris

## Architecture Overview

The system follows a layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ GameWindow  │  │ TetrisPanel │  │   SoundManager      │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┘
│                     Business Logic Layer                    │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │TetrisEngine │  │  TetrisAI   │  │GeneticAIAlgorithm   │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┘
│                      Domain Layer                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  Tetromino  │  │    Block    │  │ ProjectConstants    │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## Key Design Patterns

### 1. Model-View-Controller (MVC)
- **Model**: `TetrisEngine`, `Tetromino`, `Block` - Game state and logic
- **View**: `GameWindow`, `TetrisPanel` - UI rendering and user interaction
- **Controller**: `TetrisAI`, `GeneticAIAlgorithm` - AI decision making

### 2. Strategy Pattern
- **AI Strategy**: Different AI implementations can be swapped (currently genetic algorithm)
- **Game Mode Strategy**: Human play vs AI play vs training mode

### 3. Observer Pattern
- Game state changes notify UI components for updates
- AI training progress updates UI with statistics

### 4. Factory Pattern
- `Tetromino` creation for different piece types
- Generation creation in genetic algorithm

## Component Relationships

### Core Game Components
```
AiTetrisApplication
├── GameWindow (Main UI Container)
│   ├── TetrisPanel (Game Rendering)
│   └── SoundManager (Audio Management)
├── TetrisEngine (Game Logic)
│   ├── Tetromino (Piece Management)
│   └── Block (Individual Blocks)
└── AI System
    ├── TetrisAI (Decision Making)
    └── GeneticAIAlgorithm (Evolution)
```

### Data Flow Patterns

#### Game Loop Flow
```
User Input → TetrisPanel → TetrisEngine → Game State Update → UI Refresh
```

#### AI Decision Flow
```
Game State → TetrisAI → GeneticAIAlgorithm → Move Decision → TetrisEngine
```

#### Training Flow
```
Generation → Fitness Evaluation → Selection → Crossover → Mutation → New Generation
```

## Critical Implementation Paths

### 1. Game Engine Path
```
TetrisEngine.update() → Piece Movement → Collision Detection → Line Clearing → Score Update
```

### 2. AI Evaluation Path
```
Board State → Weight Application → Position Scoring → Optimal Move Selection
```

### 3. Genetic Algorithm Path
```
Population → Fitness Calculation → Selection → Reproduction → Mutation → New Population
```

## Key Technical Decisions

### 1. Spring Boot Integration
- **Rationale**: Provides configuration management and dependency injection
- **Impact**: Enables easy parameter configuration and component management

### 2. Java Swing UI
- **Rationale**: Cross-platform compatibility and native look-and-feel
- **Impact**: Consistent UI across different operating systems

### 3. YAML Configuration
- **Rationale**: Human-readable configuration for genetic algorithm parameters
- **Impact**: Easy experimentation without code changes

### 4. Genetic Algorithm Structure
- **Rationale**: Proven approach for optimization problems
- **Impact**: Self-improving AI that discovers optimal strategies

## Component Responsibilities

### TetrisEngine
- Game state management
- Piece movement and collision detection
- Line clearing and scoring
- Game loop coordination

### TetrisAI
- Board state evaluation
- Move decision making
- Integration with genetic algorithm

### GeneticAIAlgorithm
- Population management
- Fitness evaluation
- Selection and reproduction
- Evolution coordination

### GameWindow/TetrisPanel
- UI rendering
- User input handling
- Game state visualization
- Progress display

### SoundManager
- Audio file management
- Sound effect playback
- Background music control

## Performance Considerations

### Game Loop Optimization
- Efficient collision detection algorithms
- Minimal object allocation in update loops
- Batch rendering operations

### AI Performance
- Cached board evaluations
- Optimized fitness calculations
- Parallel generation evaluation (future enhancement)

### Memory Management
- Reuse of game objects
- Efficient data structures for board representation
- Proper cleanup of completed generations 