package com.comp2042.data;

/**
 * Record representing a move event in the game.
 * 
 * <p>This immutable record encapsulates information about a game event,
 * including the type of event (move, rotate, etc.) and the source that
 * triggered it (user input, automatic movement, etc.).
 * 
 * <p>MoveEvent objects are used throughout the game to communicate
 * player actions and system events between components.
 * 
 * @param eventType The type of event (DOWN, LEFT, RIGHT, ROTATE, etc.)
 * @param eventSource The source that triggered the event (USER, THREAD, etc.)
 * 
 * @author Rajul Kabir
 * @version 1.0
 * @see EventType
 * @see EventSource
 */
public record MoveEvent(EventType eventType, EventSource eventSource) {
}
