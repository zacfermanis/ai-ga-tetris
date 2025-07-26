# Main Menu Feature Design

## Overview

The main menu feature will create a centralized entry point for the AI-GA-Tetris application, replacing the current direct launch into game modes. This design maintains the existing architecture while adding a new presentation layer component.

## Architecture

### High-Level Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                    Main Menu Layer                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ MainMenuUI  │  │SettingsDialog│  │   PauseMenu        │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┘
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
```

### Component Relationships
```
AiTetrisApplication
├── MainMenuUI (New - Entry Point)
│   ├── SettingsDialog (New - Configuration)
│   └── PauseMenu (New - In-Game Menu)
└── GameWindow (Modified - Launched from Menu)
    ├── TetrisPanel (Modified - ESC Key Handling)
    └── SoundManager (Modified - Dynamic Configuration)
```

## Components

### 1. MainMenuUI
**Purpose**: Primary entry point and navigation hub for the application

**Responsibilities**:
- Display main menu options
- Handle user navigation
- Launch appropriate game modes
- Manage application lifecycle

**Key Methods**:
```java
public class MainMenuUI extends JFrame {
    public MainMenuUI() // Constructor
    public void showMenu() // Display main menu
    public void launchHumanMode() // Start human play
    public void launchAIWatchMode() // Start AI watch mode
    public void launchAITrainingMode() // Start AI training
    public void showSettings() // Open settings dialog
    public void exitApplication() // Clean shutdown
}
```

**UI Layout**:
```
┌─────────────────────────────────────┐
│           AI-GA-Tetris              │
│                                     │
│         [Play Tetris]               │
│                                     │
│        [Watch AI Play]              │
│                                     │
│         [Train AI]                  │
│                                     │
│          [Settings]                 │
│                                     │
│           [Exit]                    │
│                                     │
│     Sound: [ON/OFF] Toggle          │
└─────────────────────────────────────┘
```

### 2. SettingsDialog
**Purpose**: Provide GUI access to configuration options

**Responsibilities**:
- Display current configuration values
- Allow user modification of settings
- Save changes to application.yml
- Validate user input

**Key Methods**:
```java
public class SettingsDialog extends JDialog {
    public SettingsDialog(JFrame parent) // Constructor
    public void loadCurrentSettings() // Load from YAML
    public void saveSettings() // Save to YAML
    public void validateSettings() // Input validation
    public void applySettings() // Apply changes
}
```

**UI Layout**:
```
┌─────────────────────────────────────┐
│              Settings               │
│                                     │
│  Genetic Algorithm:                 │
│  Population Size: [4]               │
│  Mutation Rate: [0.05]              │
│  Runs per Evaluation: [3]           │
│                                     │
│  Training Options:                  │
│  [✓] Serialize Generations          │
│  [✓] Use Preset Population          │
│  [ ] Use Loaded Generation          │
│                                     │
│  Reproduction:                      │
│  [✓] Use Top Half Selection         │
│  [✓] Use Crossover                  │
│  [ ] Use Parents Average            │
│  [✓] Twin Prevention                │
│                                     │
│         [Save] [Cancel]             │
└─────────────────────────────────────┘
```

### 3. PauseMenu
**Purpose**: Provide in-game navigation back to main menu

**Responsibilities**:
- Handle ESC key during gameplay
- Display pause menu options
- Manage game state transitions
- Preserve game progress appropriately

**Key Methods**:
```java
public class PauseMenu extends JDialog {
    public PauseMenu(JFrame parent) // Constructor
    public void showPauseMenu() // Display pause options
    public void resumeGame() // Continue current game
    public void returnToMainMenu() // Exit to main menu
    public void saveProgress() // Save current state
}
```

**UI Layout**:
```
┌─────────────────────────────────────┐
│              Paused                 │
│                                     │
│         [Resume Game]               │
│                                     │
│      [Return to Main Menu]          │
│                                     │
│         [Save Progress]             │
│                                     │
│           [Exit Game]               │
└─────────────────────────────────────┘
```

## Data Models

### 1. GameMode Enum
```java
public enum GameMode {
    HUMAN_PLAY,      // Human controls, no AI
    AI_WATCH,        // AI controls, no training
    AI_TRAINING      // AI controls with training
}
```

### 2. MenuState Class
```java
public class MenuState {
    private GameMode selectedMode;
    private boolean soundEnabled;
    private Map<String, Object> settings;
    
    // Getters and setters
    public GameMode getSelectedMode() { return selectedMode; }
    public void setSelectedMode(GameMode mode) { selectedMode = mode; }
    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean enabled) { soundEnabled = enabled; }
    public Map<String, Object> getSettings() { return settings; }
    public void setSettings(Map<String, Object> settings) { this.settings = settings; }
}
```

## Integration Points

### 1. Application Entry Point Modification
**File**: `AiTetrisApplication.java`
**Changes**:
- Replace direct `GameWindow` creation with `MainMenuUI` launch
- Remove static configuration variables (move to dynamic loading)
- Add menu state management

**Modified Flow**:
```java
public static void main(String[] args) {
    // ... existing setup code ...
    
    SwingUtilities.invokeLater(() -> {
        MainMenuUI mainMenu = new MainMenuUI();
        mainMenu.showMenu();
    });
}
```

### 2. GameWindow Integration
**File**: `GameWindow.java`
**Changes**:
- Add constructor parameter for `GameMode`
- Add reference to parent `MainMenuUI`
- Add ESC key handling for pause menu

**Modified Constructor**:
```java
public GameWindow(GameMode mode, MainMenuUI parent, boolean useSounds) {
    this.mode = mode;
    this.parent = parent;
    this.useSounds = useSounds;
    // ... existing initialization ...
}
```

### 3. TetrisPanel Integration
**File**: `TetrisPanel.java`
**Changes**:
- Add ESC key listener for pause menu
- Add game mode awareness
- Add return to menu functionality

**New Methods**:
```java
public void addPauseMenuListener() // ESC key handling
public void showPauseMenu() // Display pause options
public void returnToMainMenu() // Exit to main menu
```

### 4. Configuration Management
**File**: New `ConfigurationManager.java`
**Purpose**: Centralized configuration handling

**Key Methods**:
```java
public class ConfigurationManager {
    public static void loadConfiguration() // Load from YAML
    public static void saveConfiguration() // Save to YAML
    public static void updateSetting(String key, Object value) // Update setting
    public static Object getSetting(String key) // Get setting value
    public static void applyConfiguration() // Apply to running app
}
```

## Error Handling

### 1. Configuration Errors
- **Scenario**: Invalid YAML configuration
- **Response**: Load default values, show warning dialog
- **Recovery**: Allow user to reset to defaults

### 2. File System Errors
- **Scenario**: Cannot save configuration file
- **Response**: Show error dialog with retry option
- **Recovery**: Attempt to save to alternative location

### 3. Resource Loading Errors
- **Scenario**: Missing audio/image files
- **Response**: Disable affected features gracefully
- **Recovery**: Continue with reduced functionality

## Testing Strategy

### 1. Unit Tests
- **MainMenuUI**: Test menu navigation and mode selection
- **SettingsDialog**: Test configuration loading/saving
- **PauseMenu**: Test pause/resume functionality
- **ConfigurationManager**: Test YAML operations

### 2. Integration Tests
- **Menu to Game Flow**: Test complete user journeys
- **Configuration Persistence**: Test settings across sessions
- **Audio Integration**: Test sound toggle functionality

### 3. User Acceptance Tests
- **Navigation Flow**: Test all menu paths
- **Configuration Changes**: Test settings modification
- **Error Scenarios**: Test error handling and recovery

## Performance Considerations

### 1. Menu Responsiveness
- **Target**: Menu transitions under 500ms
- **Implementation**: Use SwingUtilities.invokeLater for UI updates
- **Optimization**: Lazy load configuration dialogs

### 2. Memory Management
- **Strategy**: Dispose of game windows when returning to menu
- **Implementation**: Proper cleanup in GameWindow.dispose()
- **Monitoring**: Track memory usage during mode switches

### 3. Configuration Loading
- **Strategy**: Cache configuration values
- **Implementation**: Load once at startup, update on changes
- **Optimization**: Async loading for large configuration files

## Security Considerations

### 1. File System Access
- **Validation**: Validate all file paths before access
- **Permissions**: Handle insufficient permissions gracefully
- **Sanitization**: Sanitize user input in configuration fields

### 2. Configuration Validation
- **Input Validation**: Validate all configuration values
- **Range Checking**: Ensure numeric values are within valid ranges
- **Type Safety**: Prevent type conversion errors

## Migration Strategy

### 1. Backward Compatibility
- **Existing Configuration**: Preserve current application.yml structure
- **Default Values**: Use current defaults for new installations
- **Gradual Migration**: Support both old and new launch methods during transition

### 2. User Experience
- **Familiar Interface**: Maintain existing game mechanics
- **Progressive Enhancement**: Add menu features without breaking existing functionality
- **Documentation**: Update README with new menu navigation

## Success Metrics

### 1. Usability Metrics
- **Menu Navigation Time**: Average time to select game mode
- **Configuration Access**: Percentage of users who access settings
- **Error Rate**: Frequency of configuration-related errors

### 2. Performance Metrics
- **Startup Time**: Time from launch to menu display
- **Mode Switch Time**: Time to transition between modes
- **Memory Usage**: Memory footprint during normal operation

### 3. User Satisfaction
- **Ease of Use**: User feedback on menu navigation
- **Feature Discovery**: Usage of new configuration options
- **Error Recovery**: Success rate of error recovery scenarios 