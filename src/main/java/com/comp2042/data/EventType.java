package com.comp2042.data;

/**
 * Enumeration of possible game event types.
 * 
 * <p>This enum represents the different types of actions that can occur
 * in the game, used in conjunction with {@link MoveEvent} to communicate
 * game events throughout the system.
 * 
 * @author Rajul Kabir
 * @version 1.0
 * @see MoveEvent
 */
public enum EventType {
    /** Move the current piece down by one cell. */
    DOWN,
    
    /** Move the current piece left by one cell. */
    LEFT,
    
    /** Move the current piece right by one cell. */
    RIGHT,
    
    /** Rotate the current piece counter-clockwise. */
    ROTATE,
    
    /** Hold or swap the current piece with the held piece. */
    HOLD
}
