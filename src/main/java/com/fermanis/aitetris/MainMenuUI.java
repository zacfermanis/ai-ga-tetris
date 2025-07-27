package com.fermanis.aitetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Main menu user interface for the AI Tetris application.
 * Provides navigation to different game modes and configuration options.
 */
public class MainMenuUI extends JFrame {
    
    private static final Logger LOGGER = Logger.getLogger(MainMenuUI.class.getName());
    
    private static final String TITLE = "AI-GA-Tetris";
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 500;
    
    private MenuState menuState;
    private JButton humanPlayButton;
    private JButton aiWatchButton;
    private JButton aiTrainingButton;
    private JButton settingsButton;
    private JButton soundToggleButton;
    private JButton exitButton;
    
    private GameWindow currentGameWindow;
    private BufferedImage backgroundImage;
    
    /**
     * Constructor creates the main menu window
     */
    public MainMenuUI() {
        this.menuState = new MenuState();
        loadBackgroundImage();
        initializeUI();
        setupEventHandlers();
        showMenu();
    }
    
    /**
     * Load the menu background image from resources
     */
    private void loadBackgroundImage() {
        try {
            // Try to load from resources first
            InputStream inputStream = getClass().getResourceAsStream("/image/MenuBackground.png");
            if (inputStream != null) {
                backgroundImage = ImageIO.read(inputStream);
                LOGGER.info("Menu background loaded from resources");
            } else {
                // Fallback to file system
                backgroundImage = ImageIO.read(new java.io.File("src/main/resources/image/MenuBackground.png"));
                LOGGER.info("Menu background loaded from file system");
            }
        } catch (IOException e) {
            LOGGER.warning("Could not load menu background image: " + e.getMessage());
            backgroundImage = null;
        }
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setTitle(TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        
        // Set up the main panel
        JPanel mainPanel = createMainPanel();
        setContentPane(mainPanel);
        
        // Set up window listener for cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }
    
    /**
     * Create the main panel with all UI components
     * @return The configured main panel
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Enable high-quality rendering
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (backgroundImage != null) {
                    // Draw background image scaled to fit the panel
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    // Fallback to solid color background
                    g2d.setColor(new Color(25, 25, 25));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                
                g2d.dispose();
            }
        };
        panel.setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = createTitlePanel();
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Menu buttons panel
        JPanel menuPanel = createMenuPanel();
        panel.add(menuPanel, BorderLayout.CENTER);
        
        // Bottom panel with sound toggle and exit
        JPanel bottomPanel = createBottomPanel();
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create the title panel
     * @return The title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);  // Make panel transparent to show background
        panel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        
        JLabel titleLabel = new JLabel(TITLE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 255, 255));  // Pure white for maximum contrast
        
        panel.add(titleLabel);
        return panel;
    }
    
    /**
     * Create the menu buttons panel
     * @return The menu panel
     */
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);  // Make panel transparent to show background
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        
        // Create buttons
        humanPlayButton = createMenuButton("Human Play", "Play Tetris with keyboard controls");
        aiWatchButton = createMenuButton("AI Watch", "Watch trained AI play Tetris");
        aiTrainingButton = createMenuButton("AI Training", "Train AI using genetic algorithm");
        settingsButton = createMenuButton("Settings", "Configure game and AI parameters");
        
        // Add buttons to panel
        panel.add(humanPlayButton, gbc);
        panel.add(aiWatchButton, gbc);
        panel.add(aiTrainingButton, gbc);
        panel.add(settingsButton, gbc);
        
        return panel;
    }
    
    /**
     * Create the bottom panel with sound toggle and exit
     * @return The bottom panel
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);  // Make panel transparent to show background
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        
        // Sound toggle button
        soundToggleButton = createSmallButton("Sound: OFF");
        updateSoundButtonText();
        
        // Exit button
        exitButton = createSmallButton("Exit");
        
        panel.add(soundToggleButton);
        panel.add(exitButton);
        
        return panel;
    }
    
    /**
     * Create a styled menu button
     * @param text Button text
     * @param tooltip Button tooltip
     * @return The configured button
     */
    private JButton createMenuButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(300, 50));
        button.setToolTipText(tooltip);
        button.setBackground(new Color(30, 80, 150));  // Darker blue for better contrast
        button.setForeground(new Color(255, 255, 255));  // Pure white text
        button.setFocusPainted(false);
        button.setBorderPainted(false);  // Remove default border
        button.setContentAreaFilled(true);  // Ensure background is filled
        button.setOpaque(true);  // Make button opaque
        
        // Add hover effect with higher contrast
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 120, 200));  // Brighter blue on hover
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 80, 150));  // Return to darker blue
            }
        });
        
        return button;
    }
    
    /**
     * Create a small styled button
     * @param text Button text
     * @return The configured button
     */
    private JButton createSmallButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));  // Made font bold for better contrast
        button.setPreferredSize(new Dimension(100, 35));
        button.setBackground(new Color(60, 60, 60));  // Darker gray for better contrast
        button.setForeground(new Color(255, 255, 255));  // Pure white text
        button.setFocusPainted(false);
        button.setBorderPainted(false);  // Remove default border
        button.setContentAreaFilled(true);  // Ensure background is filled
        button.setOpaque(true);  // Make button opaque
        
        // Add hover effect for small buttons too
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(80, 80, 80));  // Lighter gray on hover
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 60, 60));  // Return to darker gray
            }
        });
        
        return button;
    }
    
    /**
     * Set up event handlers for all buttons
     */
    private void setupEventHandlers() {
        // Game mode buttons
        humanPlayButton.addActionListener(e -> launchHumanMode());
        aiWatchButton.addActionListener(e -> launchAIWatchMode());
        aiTrainingButton.addActionListener(e -> launchAITrainingMode());
        
        // Settings and utility buttons
        settingsButton.addActionListener(e -> showSettings());
        soundToggleButton.addActionListener(e -> toggleSound());
        exitButton.addActionListener(e -> exitApplication());
        
        // Keyboard navigation
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        // Make frame focusable for keyboard events
        setFocusable(true);
    }
    
    /**
     * Handle keyboard navigation
     * @param e The key event
     */
    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_1:
            case KeyEvent.VK_H:
                launchHumanMode();
                break;
            case KeyEvent.VK_2:
            case KeyEvent.VK_A:
                launchAIWatchMode();
                break;
            case KeyEvent.VK_3:
            case KeyEvent.VK_T:
                launchAITrainingMode();
                break;
            case KeyEvent.VK_S:
                showSettings();
                break;
            case KeyEvent.VK_M:
                toggleSound();
                break;
            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_X:
                exitApplication();
                break;
        }
    }
    
    /**
     * Update the sound button text based on current state
     */
    private void updateSoundButtonText() {
        String text = "Sound: " + (menuState.isSoundEnabled() ? "ON" : "OFF");
        soundToggleButton.setText(text);
    }
    
    /**
     * Launch human play mode
     */
    public void launchHumanMode() {
        LOGGER.info("Launching human play mode");
        menuState.setCurrentGameMode(GameMode.HUMAN_PLAY);
        launchGame();
    }
    
    /**
     * Launch AI watch mode
     */
    public void launchAIWatchMode() {
        LOGGER.info("Launching AI watch mode");
        menuState.setCurrentGameMode(GameMode.AI_WATCH);
        launchGame();
    }
    
    /**
     * Launch AI training mode
     */
    public void launchAITrainingMode() {
        LOGGER.info("Launching AI training mode");
        menuState.setCurrentGameMode(GameMode.AI_TRAINING);
        launchGame();
    }
    
    /**
     * Launch the game with current settings
     */
    private void launchGame() {
        // Apply current menu state to configuration
        menuState.applyToConfiguration();
        
        // Hide the menu
        setVisible(false);
        
        // Create and start the game window
        boolean useSounds = menuState.isSoundEnabled();
        boolean useAI = menuState.getCurrentGameMode().usesAI();
        boolean trainAI = menuState.getCurrentGameMode().isTrainingMode();
        
        currentGameWindow = new GameWindow(useSounds, useAI, trainAI);
        menuState.startGame();
        
        LOGGER.info("Game launched in " + menuState.getCurrentGameMode().getDisplayName() + " mode");
    }
    
    /**
     * Show the settings dialog
     */
    public void showSettings() {
        LOGGER.info("Opening settings dialog");
        // TODO: Implement SettingsDialog
        JOptionPane.showMessageDialog(this, 
            "Settings dialog will be implemented in the next phase.", 
            "Settings", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Toggle sound on/off
     */
    public void toggleSound() {
        boolean newState = menuState.toggleSound();
        updateSoundButtonText();
        menuState.markSettingsModified();
        
        LOGGER.info("Sound toggled to: " + (newState ? "ON" : "OFF"));
    }
    
    /**
     * Exit the application
     */
    public void exitApplication() {
        LOGGER.info("Exiting application");
        
        // Clean up any running game
        if (currentGameWindow != null) {
            currentGameWindow.dispose();
            currentGameWindow = null;
        }
        
        // Exit the application
        System.exit(0);
    }
    
    /**
     * Show the main menu
     */
    public void showMenu() {
        // Load current state from configuration
        menuState.loadFromConfiguration();
        updateSoundButtonText();
        
        // Make sure the frame is visible and focused
        setVisible(true);
        requestFocus();
        
        LOGGER.info("Main menu displayed");
    }
    
    /**
     * Return to main menu from game
     */
    public void returnToMenu() {
        LOGGER.info("Returning to main menu");
        
        // Clean up current game window
        if (currentGameWindow != null) {
            currentGameWindow.dispose();
            currentGameWindow = null;
        }
        
        // End the game
        menuState.endGame();
        
        // Show the menu
        showMenu();
    }
    
    /**
     * Get the current menu state
     * @return The menu state
     */
    public MenuState getMenuState() {
        return menuState;
    }
} 