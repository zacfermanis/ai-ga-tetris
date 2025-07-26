package com.fermanis.aitetris;

import java.util.logging.Logger;

/**
 * State management class for menu and application state.
 * Manages the current game mode, sound settings, and configuration state.
 */
public class MenuState {
    
    private static final Logger LOGGER = Logger.getLogger(MenuState.class.getName());
    
    private GameMode currentGameMode;
    private boolean soundEnabled;
    private boolean settingsModified;
    private boolean gameInProgress;
    
    /**
     * Default constructor initializes with default values
     */
    public MenuState() {
        this.currentGameMode = GameMode.HUMAN_PLAY;
        this.soundEnabled = ConfigurationManager.getBooleanSetting("app.use_sounds", false);
        this.settingsModified = false;
        this.gameInProgress = false;
    }
    
    /**
     * Constructor with specific initial values
     * @param initialGameMode The initial game mode
     * @param soundEnabled Whether sound is enabled
     */
    public MenuState(GameMode initialGameMode, boolean soundEnabled) {
        this.currentGameMode = initialGameMode != null ? initialGameMode : GameMode.HUMAN_PLAY;
        this.soundEnabled = soundEnabled;
        this.settingsModified = false;
        this.gameInProgress = false;
    }
    
    /**
     * Get the current game mode
     * @return The current game mode
     */
    public GameMode getCurrentGameMode() {
        return currentGameMode;
    }
    
    /**
     * Set the current game mode
     * @param gameMode The new game mode
     */
    public void setCurrentGameMode(GameMode gameMode) {
        if (GameMode.isValid(gameMode)) {
            this.currentGameMode = gameMode;
            LOGGER.info("Game mode changed to: " + gameMode.getDisplayName());
        } else {
            LOGGER.warning("Invalid game mode provided: " + gameMode);
        }
    }
    
    /**
     * Check if sound is enabled
     * @return true if sound is enabled
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    /**
     * Set sound enabled state
     * @param soundEnabled Whether sound should be enabled
     */
    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
        LOGGER.info("Sound " + (soundEnabled ? "enabled" : "disabled"));
    }
    
    /**
     * Toggle sound enabled state
     * @return The new sound state
     */
    public boolean toggleSound() {
        this.soundEnabled = !this.soundEnabled;
        LOGGER.info("Sound toggled to: " + (soundEnabled ? "enabled" : "disabled"));
        return this.soundEnabled;
    }
    
    /**
     * Check if settings have been modified
     * @return true if settings have been modified
     */
    public boolean isSettingsModified() {
        return settingsModified;
    }
    
    /**
     * Set settings modified flag
     * @param settingsModified Whether settings have been modified
     */
    public void setSettingsModified(boolean settingsModified) {
        this.settingsModified = settingsModified;
    }
    
    /**
     * Mark settings as modified
     */
    public void markSettingsModified() {
        this.settingsModified = true;
        LOGGER.info("Settings marked as modified");
    }
    
    /**
     * Check if a game is currently in progress
     * @return true if a game is in progress
     */
    public boolean isGameInProgress() {
        return gameInProgress;
    }
    
    /**
     * Set game in progress state
     * @param gameInProgress Whether a game is in progress
     */
    public void setGameInProgress(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
        LOGGER.info("Game in progress: " + gameInProgress);
    }
    
    /**
     * Start a new game
     */
    public void startGame() {
        this.gameInProgress = true;
        LOGGER.info("Game started in mode: " + currentGameMode.getDisplayName());
    }
    
    /**
     * End the current game
     */
    public void endGame() {
        this.gameInProgress = false;
        LOGGER.info("Game ended");
    }
    
    /**
     * Get configuration settings for the current game mode
     * @return Configuration map for the current mode
     */
    public java.util.Map<String, Object> getModeConfiguration() {
        java.util.Map<String, Object> config = new java.util.HashMap<>();
        
        // Base configuration
        config.put("use_sounds", soundEnabled);
        
        // Mode-specific configuration
        switch (currentGameMode) {
            case HUMAN_PLAY:
                config.put("use_ai", false);
                config.put("train_ai", false);
                break;
            case AI_WATCH:
                config.put("use_ai", true);
                config.put("train_ai", false);
                break;
            case AI_TRAINING:
                config.put("use_ai", true);
                config.put("train_ai", true);
                break;
        }
        
        return config;
    }
    
    /**
     * Apply current state to configuration manager
     */
    public void applyToConfiguration() {
        // Update runtime configuration with current state
        ConfigurationManager.updateSetting("app.use_sounds", soundEnabled);
        
        switch (currentGameMode) {
            case HUMAN_PLAY:
                ConfigurationManager.updateSetting("app.use_ai", false);
                ConfigurationManager.updateSetting("app.train_ai", false);
                break;
            case AI_WATCH:
                ConfigurationManager.updateSetting("app.use_ai", true);
                ConfigurationManager.updateSetting("app.train_ai", false);
                break;
            case AI_TRAINING:
                ConfigurationManager.updateSetting("app.use_ai", true);
                ConfigurationManager.updateSetting("app.train_ai", true);
                break;
        }
        
        settingsModified = false;
        LOGGER.info("Menu state applied to configuration");
    }
    
    /**
     * Load state from configuration manager
     */
    public void loadFromConfiguration() {
        this.soundEnabled = ConfigurationManager.getBooleanSetting("app.use_sounds", false);
        
        boolean useAI = ConfigurationManager.getBooleanSetting("app.use_ai", false);
        boolean trainAI = ConfigurationManager.getBooleanSetting("app.train_ai", false);
        
        if (useAI && trainAI) {
            this.currentGameMode = GameMode.AI_TRAINING;
        } else if (useAI) {
            this.currentGameMode = GameMode.AI_WATCH;
        } else {
            this.currentGameMode = GameMode.HUMAN_PLAY;
        }
        
        settingsModified = false;
        LOGGER.info("Menu state loaded from configuration");
    }
    
    /**
     * Reset state to default values
     */
    public void resetToDefaults() {
        this.currentGameMode = GameMode.HUMAN_PLAY;
        this.soundEnabled = false;
        this.settingsModified = false;
        this.gameInProgress = false;
        LOGGER.info("Menu state reset to defaults");
    }
    
    /**
     * Validate the current state
     * @return true if the state is valid
     */
    public boolean isValid() {
        if (!GameMode.isValid(currentGameMode)) {
            LOGGER.warning("Invalid game mode in state");
            return false;
        }
        
        return true;
    }
    
    /**
     * Get a string representation of the current state
     * @return String representation of the state
     */
    @Override
    public String toString() {
        return String.format("MenuState{gameMode=%s, soundEnabled=%s, settingsModified=%s, gameInProgress=%s}",
                currentGameMode.getDisplayName(), soundEnabled, settingsModified, gameInProgress);
    }
    
    /**
     * Create a copy of the current state
     * @return A new MenuState with the same values
     */
    public MenuState copy() {
        MenuState copy = new MenuState(this.currentGameMode, this.soundEnabled);
        copy.settingsModified = this.settingsModified;
        copy.gameInProgress = this.gameInProgress;
        return copy;
    }
} 