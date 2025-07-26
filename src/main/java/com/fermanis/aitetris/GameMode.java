package com.fermanis.aitetris;

/**
 * Enum representing different game modes available in the AI Tetris application.
 * Each mode provides a different gameplay experience and AI interaction level.
 */
public enum GameMode {
    /**
     * Human play mode - player controls the game manually
     */
    HUMAN_PLAY("Human Play"),
    
    /**
     * AI watch mode - observe trained AI playing the game
     */
    AI_WATCH("AI Watch"),
    
    /**
     * AI training mode - run genetic algorithm to train and evolve AI
     */
    AI_TRAINING("AI Training");

    private final String displayName;

    /**
     * Constructor for GameMode enum
     * @param displayName The human-readable name for display in UI
     */
    GameMode(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the display name for UI purposes
     * @return Human-readable name for this game mode
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the display name for UI purposes (alias for getDisplayName)
     * @return Human-readable name for this game mode
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Check if this mode uses AI
     * @return true if AI is involved in this mode
     */
    public boolean usesAI() {
        return this == AI_WATCH || this == AI_TRAINING;
    }

    /**
     * Check if this mode involves AI training
     * @return true if this mode trains the AI
     */
    public boolean isTrainingMode() {
        return this == AI_TRAINING;
    }

    /**
     * Check if this mode is for human play
     * @return true if this mode is for human control
     */
    public boolean isHumanMode() {
        return this == HUMAN_PLAY;
    }

    /**
     * Validate if a game mode is valid
     * @param mode The mode to validate
     * @return true if the mode is not null and is a valid enum value
     */
    public static boolean isValid(GameMode mode) {
        return mode != null;
    }
} 