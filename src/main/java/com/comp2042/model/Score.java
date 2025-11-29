package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the game score using JavaFX properties for reactive UI binding.
 * 
 * <p>This class provides a score that can be observed by UI components.
 * The score is stored as a JavaFX {@link IntegerProperty}, allowing automatic
 * UI updates when the score changes.
 * 
 * <p>The score starts at 0 and can be incremented using {@link #add(int)}.
 * It can be reset to 0 using {@link #reset()}.
 * 
 * @author Rajul Kabir
 * @version 1.0
 * @see IntegerProperty
 */
public final class Score {

    /** The current score value, observable via JavaFX properties. */
    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Gets the score property for UI binding.
     * 
     * <p>This property can be bound to UI components to automatically
     * update the display when the score changes.
     * 
     * @return The score property
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Adds points to the current score.
     * 
     * @param i The number of points to add (can be negative)
     */
    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    /**
     * Resets the score to 0.
     * 
     * <p>This is typically called when starting a new game.
     */
    public void reset() {
        score.setValue(0);
    }
}
