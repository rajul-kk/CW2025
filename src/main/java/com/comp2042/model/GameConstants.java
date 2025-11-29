package com.comp2042.model;

/**
 * Centralized constants for the Tetris game application.
 * 
 * <p>This utility class contains all project-wide constants organized into
 * logical sections:
 * <ul>
 *   <li>UI Dimensions - Window and dialog sizes</li>
 *   <li>Game Board Dimensions - Board size and layout</li>
 *   <li>Brick/Block Sizes - Visual sizing for game elements</li>
 *   <li>Animation Timing - Durations for visual effects</li>
 *   <li>File Names - Resource and data file names</li>
 *   <li>CSS Classes - Style class names for UI elements</li>
 * </ul>
 * 
 * <p>All constants are public static final and should be accessed directly
 * via the class name. This class cannot be instantiated.
 * 
 * @author Rajul Kabir
 * @version 1.0
 */
public final class GameConstants {
    
    // Prevent instantiation
    private GameConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
    
    // ==================== UI Dimensions ====================
    
    /** Main application window width */
    public static final int WINDOW_WIDTH = 600;
    
    /** Main application window height */
    public static final int WINDOW_HEIGHT = 560;
    
    /** Pause menu dialog width */
    public static final int PAUSE_MENU_WIDTH = 250;
    
    /** Pause menu dialog height */
    public static final int PAUSE_MENU_HEIGHT = 350;
    
    /** Controls dialog width */
    public static final int CONTROLS_DIALOG_WIDTH = 480;
    
    /** Controls dialog height */
    public static final int CONTROLS_DIALOG_HEIGHT = 520;
    
    // ==================== Game Board Dimensions ====================
    
    /** Game board height (rows) */
    public static final int BOARD_HEIGHT = 25;
    
    /** Game board width (columns) */
    public static final int BOARD_WIDTH = 10;
    
    /** Number of hidden rows at the top of the board */
    public static final int HIDDEN_ROW_OFFSET = 2;
    
    // ==================== Brick/Block Sizes ====================
    
    /** Size of bricks/blocks in the game board (pixels) */
    public static final int BRICK_SIZE = 20;
    
    /** Size of bricks in preview panes (next blocks, hold block) */
    public static final int PREVIEW_BRICK_SIZE = 15;
    
    // ==================== Timing Constants ====================
    
    /** Duration of each game tick (automatic block movement) in milliseconds */
    public static final int GAME_TICK_DURATION_MS = 400;
    
    /** Cooldown period for hard drop in milliseconds */
    public static final long DROP_COOLDOWN_MS = 300;
    
    /** Duration of lock pulse animation in milliseconds */
    public static final int LOCK_PULSE_DURATION_MS = 150;
    
    /** Duration of board shake animation in milliseconds */
    public static final int SHAKE_DURATION_MS = 100;
    
    /** Duration of score notification animation in milliseconds */
    public static final int NOTIFICATION_DURATION_MS = 2500;
    
    // ==================== Animation Constants ====================
    
    /** Starting scale for lock pulse animation */
    public static final double LOCK_PULSE_SCALE_FROM = 1.0;
    
    /** Ending scale for lock pulse animation */
    public static final double LOCK_PULSE_SCALE_TO = 1.2;
    
    /** Number of cycles for lock pulse animation */
    public static final int LOCK_PULSE_CYCLES = 2;
    
    /** Number of shake frames for board shake effect */
    public static final int SHAKE_COUNT = 5;
    
    /** Maximum offset for board shake effect (pixels) */
    public static final double SHAKE_MAX_OFFSET = 3.0;
    
    // ==================== CSS Class Names ====================
    
    /** CSS class for pause menu buttons */
    public static final String CSS_PAUSE_MENU_BUTTON = "pause-menu-button";
    
    /** CSS class for controls dialog items */
    public static final String CSS_CONTROLS_ITEM = "controls-item";
    
    /** CSS class for controls dialog section headers */
    public static final String CSS_CONTROLS_SECTION_HEADER = "controls-section-header";
    
    // ==================== File Names ====================
    
    /** High score file name */
    public static final String HIGHSCORE_FILE = "highscore.txt";
    
    // ==================== Application Info ====================
    
    /** Application title */
    public static final String APP_TITLE = "TetrisJFX";
}

