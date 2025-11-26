package com.comp2042.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class HighScoreManager {
    
    private static final String PHANTOM_HIGHSCORE_FILE = "highscore_phantom.txt";
    
    /**
     * Loads the high score from the file.
     * @param isPhantomMode true for phantom mode high score, false for classic mode
     * @return The high score, or 0 if the file doesn't exist or has an error
     */
    public static int loadHighScore(boolean isPhantomMode) {
        String filename = isPhantomMode ? PHANTOM_HIGHSCORE_FILE : GameConstants.HIGHSCORE_FILE;
        File file = new File(filename);
        
        if (!file.exists()) {
            return 0;
        }
        
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist, return 0
            return 0;
        } catch (Exception e) {
            // Any other error, return 0
            return 0;
        }
        
        return 0;
    }
    
    /**
     * Loads the high score from the file (classic mode, for backward compatibility).
     * @return The high score, or 0 if the file doesn't exist or has an error
     */
    public static int loadHighScore() {
        return loadHighScore(false);
    }
    
    /**
     * Saves the high score to the file, overwriting the old one.
     * @param score The score to save
     * @param isPhantomMode true for phantom mode high score, false for classic mode
     */
    public static void saveHighScore(int score, boolean isPhantomMode) {
        String filename = isPhantomMode ? PHANTOM_HIGHSCORE_FILE : GameConstants.HIGHSCORE_FILE;
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            // If saving fails, we can't do much about it
            // In a production app, you might want to log this
            System.err.println("Failed to save high score: " + e.getMessage());
        }
    }
    
    /**
     * Saves the high score to the file (classic mode, for backward compatibility).
     * @param score The score to save
     */
    public static void saveHighScore(int score) {
        saveHighScore(score, false);
    }
}

