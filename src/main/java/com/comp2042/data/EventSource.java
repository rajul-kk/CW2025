package com.comp2042.data;

/**
 * Enumeration of possible sources for game events.
 * 
 * <p>This enum indicates whether an event was triggered by user input
 * or by the automatic game thread (e.g., automatic downward movement).
 * This distinction is important for scoring and game logic.
 * 
 * @author Rajul Kabir
 * @version 1.0
 * @see MoveEvent
 */
public enum EventSource {
    /** Event triggered by user input (keyboard). */
    USER,
    
    /** Event triggered by the automatic game thread (timeline). */
    THREAD
}
