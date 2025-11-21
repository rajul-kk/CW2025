package com.comp2042;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class HighScoreManager {
    
    private static final String HIGHSCORE_FILE = "highscore.txt";
    
    /**
     * Loads the high score from the file.
     * @return The high score, or 0 if the file doesn't exist or has an error
     */
    public static int loadHighScore() {
        File file = new File(HIGHSCORE_FILE);
        
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
     * Saves the high score to the file, overwriting the old one.
     * @param score The score to save
     */
    public static void saveHighScore(int score) {
        try (FileWriter writer = new FileWriter(HIGHSCORE_FILE)) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            // If saving fails, we can't do much about it
            // In a production app, you might want to log this
            System.err.println("Failed to save high score: " + e.getMessage());
        }
    }
}

