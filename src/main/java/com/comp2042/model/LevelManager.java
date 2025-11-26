package com.comp2042.model;

/**
 * Manages level progression and lines cleared tracking for the Tetris game.
 * Handles level calculation based on lines cleared and drop interval calculation.
 */
public class LevelManager {
    
    private int level = 1;
    private int totalLinesCleared = 0;
    private int dropInterval = 1000; // Initial speed in ms
    
    /** Number of lines required to advance to the next level */
    private static final int LINES_PER_LEVEL = 10;
    
    /** Initial drop interval in milliseconds */
    private static final int INITIAL_DROP_INTERVAL = 1000;
    
    /** Minimum drop interval in milliseconds (maximum speed) */
    private static final int MIN_DROP_INTERVAL = 10;
    
    /**
     * Creates a new LevelManager with initial values.
     */
    public LevelManager() {
        reset();
    }
    
    /**
     * Resets the level manager to initial state (level 1, 0 lines cleared).
     */
    public void reset() {
        level = 1;
        totalLinesCleared = 0;
        dropInterval = INITIAL_DROP_INTERVAL;
    }
    
    /**
     * Adds cleared lines and updates level if necessary.
     * @param linesCleared The number of lines cleared in this clear
     * @return LevelUpdateResult containing the updated level, total lines, and drop interval
     */
    public LevelUpdateResult addLinesCleared(int linesCleared) {
        if (linesCleared <= 0) {
            return new LevelUpdateResult(level, totalLinesCleared, dropInterval, false);
        }
        
        totalLinesCleared += linesCleared;
        int newLevel = calculateLevel(totalLinesCleared);
        boolean levelIncreased = false;
        
        if (newLevel > level) {
            level = newLevel;
            dropInterval = calculateDropInterval(level);
            levelIncreased = true;
        }
        
        return new LevelUpdateResult(level, totalLinesCleared, dropInterval, levelIncreased);
    }
    
    /**
     * Calculates the current level based on total lines cleared.
     * @param totalLines The total number of lines cleared
     * @return The current level (1-based)
     */
    private int calculateLevel(int totalLines) {
        return (totalLines / LINES_PER_LEVEL) + 1;
    }
    
    /**
     * Calculates the drop interval based on the current level using the Modern Tetris Guideline formula.
     * This formula is exponential, making the game start slow but get fast very quickly.
     * @param currentLevel The current level
     * @return The drop interval in milliseconds
     */
    private int calculateDropInterval(int currentLevel) {
        // Modern Tetris Guideline formula: seconds = (0.8 - ((level - 1) * 0.01))^(level - 1)
        double seconds = Math.pow((0.8 - ((currentLevel - 1) * 0.01)), (currentLevel - 1));
        
        // Convert to milliseconds
        int calculatedInterval = (int) (seconds * 1000);
        
        // Clamp to minimum to prevent game loop from breaking
        return Math.max(MIN_DROP_INTERVAL, calculatedInterval);
    }
    
    /**
     * Gets the current level.
     * @return The current level
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Gets the total number of lines cleared.
     * @return The total lines cleared
     */
    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }
    
    /**
     * Gets the current drop interval.
     * @return The drop interval in milliseconds
     */
    public int getDropInterval() {
        return dropInterval;
    }
    
    /**
     * Result object containing level update information.
     */
    public record LevelUpdateResult(
        int level,
        int totalLinesCleared,
        int dropInterval,
        boolean levelIncreased
    ) {}
}

