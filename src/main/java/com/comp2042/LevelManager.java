package com.comp2042;

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
    private static final int MIN_DROP_INTERVAL = 100;
    
    /** Drop interval reduction per level in milliseconds */
    private static final int DROP_INTERVAL_REDUCTION_PER_LEVEL = 100;
    
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
     * Calculates the drop interval based on the current level.
     * @param currentLevel The current level
     * @return The drop interval in milliseconds
     */
    private int calculateDropInterval(int currentLevel) {
        int calculatedInterval = INITIAL_DROP_INTERVAL - ((currentLevel - 1) * DROP_INTERVAL_REDUCTION_PER_LEVEL);
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
    public static class LevelUpdateResult {
        private final int level;
        private final int totalLinesCleared;
        private final int dropInterval;
        private final boolean levelIncreased;
        
        public LevelUpdateResult(int level, int totalLinesCleared, int dropInterval, boolean levelIncreased) {
            this.level = level;
            this.totalLinesCleared = totalLinesCleared;
            this.dropInterval = dropInterval;
            this.levelIncreased = levelIncreased;
        }
        
        public int getLevel() {
            return level;
        }
        
        public int getTotalLinesCleared() {
            return totalLinesCleared;
        }
        
        public int getDropInterval() {
            return dropInterval;
        }
        
        public boolean isLevelIncreased() {
            return levelIncreased;
        }
    }
}

