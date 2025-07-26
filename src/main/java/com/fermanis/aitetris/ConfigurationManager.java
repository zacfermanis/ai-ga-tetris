package com.fermanis.aitetris;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Centralized configuration management system for the AI Tetris application.
 * Works with Spring Boot's configuration system and provides a simplified interface
 * for accessing and updating configuration settings.
 */
@Component
public class ConfigurationManager {
    
    private static final Logger LOGGER = Logger.getLogger(ConfigurationManager.class.getName());
    
    private static Environment environment;
    private static Map<String, Object> runtimeConfiguration;
    private static boolean isInitialized = false;
    
    @Autowired
    public void setEnvironment(Environment env) {
        environment = env;
    }
    
    /**
     * Initialize the configuration manager
     */
    public static void initialize() {
        if (!isInitialized) {
            runtimeConfiguration = new HashMap<>();
            isInitialized = true;
            LOGGER.info("Configuration manager initialized");
        }
    }
    
    /**
     * Get a configuration setting by key from Spring Environment
     * @param key The configuration key (supports dot notation for nested properties)
     * @return The configuration value, or null if not found
     */
    public static String getSetting(String key) {
        if (!isInitialized) {
            initialize();
        }
        
        if (environment == null || key == null) {
            return null;
        }
        
        return environment.getProperty(key);
    }
    
    /**
     * Get a configuration setting with a default value
     * @param key The configuration key
     * @param defaultValue The default value if key is not found
     * @return The configuration value or default value
     */
    public static String getSetting(String key, String defaultValue) {
        String value = getSetting(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Get a boolean configuration setting
     * @param key The configuration key
     * @param defaultValue The default value if key is not found
     * @return The boolean configuration value
     */
    public static boolean getBooleanSetting(String key, boolean defaultValue) {
        if (!isInitialized) {
            initialize();
        }
        
        if (environment == null) {
            return defaultValue;
        }
        
        return environment.getProperty(key, Boolean.class, defaultValue);
    }
    
    /**
     * Get an integer configuration setting
     * @param key The configuration key
     * @param defaultValue The default value if key is not found
     * @return The integer configuration value
     */
    public static int getIntSetting(String key, int defaultValue) {
        if (!isInitialized) {
            initialize();
        }
        
        if (environment == null) {
            return defaultValue;
        }
        
        return environment.getProperty(key, Integer.class, defaultValue);
    }
    
    /**
     * Get a double configuration setting
     * @param key The configuration key
     * @param defaultValue The default value if key is not found
     * @return The double configuration value
     */
    public static double getDoubleSetting(String key, double defaultValue) {
        if (!isInitialized) {
            initialize();
        }
        
        if (environment == null) {
            return defaultValue;
        }
        
        return environment.getProperty(key, Double.class, defaultValue);
    }
    
    /**
     * Update a runtime configuration setting (does not persist to file)
     * @param key The configuration key
     * @param value The new value to set
     */
    public static void updateSetting(String key, Object value) {
        if (!isInitialized) {
            initialize();
        }
        
        if (runtimeConfiguration == null) {
            runtimeConfiguration = new HashMap<>();
        }
        
        runtimeConfiguration.put(key, value);
        LOGGER.info("Runtime configuration setting updated: " + key + " = " + value);
    }
    
    /**
     * Get a runtime configuration setting
     * @param key The configuration key
     * @return The runtime configuration value, or null if not found
     */
    public static Object getRuntimeSetting(String key) {
        if (!isInitialized) {
            initialize();
        }
        
        if (runtimeConfiguration == null) {
            return null;
        }
        
        return runtimeConfiguration.get(key);
    }
    
    /**
     * Get a runtime configuration setting with a default value
     * @param key The configuration key
     * @param defaultValue The default value if key is not found
     * @return The runtime configuration value or default value
     */
    public static Object getRuntimeSetting(String key, Object defaultValue) {
        Object value = getRuntimeSetting(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Validate configuration values
     * @return true if configuration is valid
     */
    public static boolean validateConfiguration() {
        if (!isInitialized) {
            initialize();
        }
        
        try {
            // Validate population size
            int population = getIntSetting("genetic_algo.population", 4);
            if (population <= 0 || population % 4 != 0) {
                LOGGER.warning("Population size must be positive and divisible by 4");
                return false;
            }
            
            // Validate mutation rate
            double mutationRate = getDoubleSetting("genetic_algo.mutuation_rate", 0.05);
            if (mutationRate < 0.0 || mutationRate > 1.0) {
                LOGGER.warning("Mutation rate must be between 0.0 and 1.0");
                return false;
            }
            
            // Validate runs per evaluation
            int runsPerEval = getIntSetting("genetic_algo.runs_per_eval", 3);
            if (runsPerEval <= 0) {
                LOGGER.warning("Runs per evaluation must be positive");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Configuration validation failed", e);
            return false;
        }
    }
    
    /**
     * Get the current runtime configuration map
     * @return The runtime configuration map
     */
    public static Map<String, Object> getRuntimeConfiguration() {
        if (!isInitialized) {
            initialize();
        }
        return new HashMap<>(runtimeConfiguration);
    }
    
    /**
     * Clear all runtime configuration settings
     */
    public static void clearRuntimeConfiguration() {
        if (runtimeConfiguration != null) {
            runtimeConfiguration.clear();
            LOGGER.info("Runtime configuration cleared");
        }
    }
} 