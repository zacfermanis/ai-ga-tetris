package com.fermanis.aitetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Splash screen that displays the AI-GA-Tetris logo with fade in/out animations.
 * Shows for 6 seconds total (3 seconds fade in, 3 seconds fade out) before transitioning to main menu.
 */
public class SplashScreen extends JWindow {
    
    private static final Logger LOGGER = Logger.getLogger(SplashScreen.class.getName());
    
    private static final int FADE_IN_DURATION = 3000;  // 3 seconds
    private static final int FADE_OUT_DURATION = 3000; // 3 seconds
    private static final int TOTAL_DURATION = FADE_IN_DURATION + FADE_OUT_DURATION;
    
    private BufferedImage logoImage;
    private float currentAlpha = 0.0f;
    private Timer animationTimer;
    private long startTime;
    private boolean isFadingIn = true;
    
    private MainMenuUI mainMenu;
    
    /**
     * Constructor creates the splash screen with logo
     */
    public SplashScreen() {
        loadLogoImage();
        initializeUI();
        setupAnimation();
    }
    
    /**
     * Load the AI-GA-Tetris logo image from resources
     */
    private void loadLogoImage() {
        try {
            // Try to load from resources first
            InputStream inputStream = getClass().getResourceAsStream("/AI-GA-Tetris_Logo.png");
            if (inputStream != null) {
                logoImage = ImageIO.read(inputStream);
                LOGGER.info("Logo loaded from resources");
            } else {
                // Fallback to file system
                logoImage = ImageIO.read(new java.io.File("AI-GA-Tetris_Logo.png"));
                LOGGER.info("Logo loaded from file system");
            }
        } catch (IOException e) {
            LOGGER.warning("Could not load logo image: " + e.getMessage());
            // Create a fallback logo with text
            createFallbackLogo();
        }
    }
    
    /**
     * Create a fallback logo if the image cannot be loaded
     */
    private void createFallbackLogo() {
        logoImage = new BufferedImage(400, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = logoImage.createGraphics();
        
        // Set rendering hints for better text quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw background
        g2d.setColor(new Color(25, 25, 25));
        g2d.fillRect(0, 0, 400, 200);
        
        // Draw text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        FontMetrics fm = g2d.getFontMetrics();
        String title = "AI-GA-Tetris";
        int x = (400 - fm.stringWidth(title)) / 2;
        int y = 100;
        g2d.drawString(title, x, y);
        
        g2d.dispose();
    }
    
    /**
     * Initialize the splash screen UI
     */
    private void initializeUI() {
        // Set up the window with dynamic sizing based on logo
        int windowWidth = 600;
        int windowHeight = 400;
        
        if (logoImage != null) {
            // Calculate scaled dimensions to fit the logo within the window
            double scaleX = (double) windowWidth / logoImage.getWidth();
            double scaleY = (double) windowHeight / logoImage.getHeight();
            double scale = Math.min(scaleX, scaleY) * 0.8; // 80% of max size for padding
            
            int scaledWidth = (int) (logoImage.getWidth() * scale);
            int scaledHeight = (int) (logoImage.getHeight() * scale);
            
            // Update window size to accommodate the scaled logo with padding
            windowWidth = scaledWidth + 100; // 50px padding on each side
            windowHeight = scaledHeight + 100; // 50px padding on each side
        }
        
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        
        // Create content pane with custom painting
        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Enable alpha blending
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentAlpha));
                
                // Enable high-quality rendering
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill background
                g2d.setColor(new Color(25, 25, 25));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw logo centered and scaled
                if (logoImage != null) {
                    // Calculate scaled dimensions
                    double scaleX = (double) (getWidth() - 100) / logoImage.getWidth();
                    double scaleY = (double) (getHeight() - 100) / logoImage.getHeight();
                    double scale = Math.min(scaleX, scaleY);
                    
                    int scaledWidth = (int) (logoImage.getWidth() * scale);
                    int scaledHeight = (int) (logoImage.getHeight() * scale);
                    
                    // Center the logo
                    int x = (getWidth() - scaledWidth) / 2;
                    int y = (getHeight() - scaledHeight) / 2;
                    
                    g2d.drawImage(logoImage, x, y, scaledWidth, scaledHeight, null);
                }
                
                g2d.dispose();
            }
        });
        
        // Set background to be transparent
        getContentPane().setBackground(new Color(25, 25, 25));
    }
    
    /**
     * Set up the fade animation timer
     */
    private void setupAnimation() {
        startTime = System.currentTimeMillis();
        
        animationTimer = new Timer(16, new ActionListener() { // ~60 FPS
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                
                if (elapsed >= TOTAL_DURATION) {
                    // Animation complete, transition to main menu
                    finishSplash();
                } else if (elapsed < FADE_IN_DURATION) {
                    // Fade in phase
                    currentAlpha = (float) elapsed / FADE_IN_DURATION;
                } else {
                    // Fade out phase
                    long fadeOutElapsed = elapsed - FADE_IN_DURATION;
                    currentAlpha = 1.0f - ((float) fadeOutElapsed / FADE_OUT_DURATION);
                }
                
                // Ensure alpha is within bounds
                currentAlpha = Math.max(0.0f, Math.min(1.0f, currentAlpha));
                
                // Repaint to show the new alpha
                repaint();
            }
        });
    }
    
    /**
     * Start the splash screen animation
     */
    public void start() {
        LOGGER.info("Starting splash screen");
        setVisible(true);
        animationTimer.start();
    }
    
    /**
     * Finish the splash screen and transition to main menu
     */
    private void finishSplash() {
        LOGGER.info("Splash screen complete, transitioning to main menu");
        animationTimer.stop();
        setVisible(false);
        dispose();
        
        // Create and show main menu
        SwingUtilities.invokeLater(() -> {
            mainMenu = new MainMenuUI();
            mainMenu.showMenu();
        });
    }
    
    /**
     * Get the main menu instance (for testing purposes)
     * @return The main menu instance
     */
    public MainMenuUI getMainMenu() {
        return mainMenu;
    }
} 