package com.fermanis.aitetris;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

import static com.fermanis.aitetris.ProjectConstants.formatStackTrace;

@SpringBootApplication
public class AiTetrisApplication {

    /*Errors go to console if true, otherwise go to GUI logger.*/
    public static final boolean REPORT_TO_CONSOLE = true;

    // Configuration is now handled by ConfigurationManager
    // These static variables are kept for backward compatibility
    // but will be populated from ConfigurationManager when needed
    
    /* Configuration Variables - Backward Compatibility */
    public static boolean USE_SOUNDS = false;
    public static boolean USE_AI = false;
    public static boolean TRAIN_AI = false;
    public static int POPULATION = 16;

    @Value("${app.use_sounds}")
    public void setUseSounds(boolean sounds) {
        USE_SOUNDS = sounds;
        // Update ConfigurationManager for consistency
        ConfigurationManager.updateSetting("app.use_sounds", sounds);
    }

    @Value("${app.use_ai}")
    public void setUseAI(boolean ai) {
        USE_AI = ai;
        // Update ConfigurationManager for consistency
        ConfigurationManager.updateSetting("app.use_ai", ai);
    }

    @Value("${app.train_ai}")
    public void setUseGenetic(boolean genetic) {
        TRAIN_AI = genetic;
        // Update ConfigurationManager for consistency
        ConfigurationManager.updateSetting("app.train_ai", genetic);
    }

    // How many candidates are there in a generation?
    // Must be a multiple of 4.
    @Value("${genetic_algo.population}")
    public void setPopulation(int pop) {
        POPULATION = pop;
        // Update ConfigurationManager for consistency
        ConfigurationManager.updateSetting("genetic_algo.population", pop);
    }

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(AiTetrisApplication.class, args);
        System.setErr(System.out);

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable t) {
        }

        //Better exception catching.
        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {

                    public void uncaughtException(Thread t, Throwable e) {

                        if (REPORT_TO_CONSOLE) {
                            e.printStackTrace();
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "Java version: " + System.getProperty("java.version")
                                            + "\nOperating System: " + System.getProperty("os.name")
                                            + "\nFatal exception in thread: " + t.getName()
                                            + "\nException: " + e.getClass().getName()
                                            + "\nReason given: " + e.getMessage()
                                            + "\n\n" + formatStackTrace(e.getStackTrace()));
                        }
                        System.exit(1);
                    }
                });

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Thread(new Runnable() {
                    public void run() {
                        // Initialize configuration manager
                        ConfigurationManager.initialize();
                        
                        // Launch main menu instead of direct game
                        MainMenuUI mainMenu = new MainMenuUI();
                    }
                }).start();
            }
        });


    }



}
