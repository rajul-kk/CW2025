package com.comp2042.model;

import com.comp2042.util.MatrixOperations;

/**
 * Immutable data class containing information about the next rotation state of a piece.
 * 
 * <p>This class is used by the rotation system to represent a piece's shape
 * at a specific rotation state. It contains both the shape matrix and the
 * rotation position index.
 * 
 * <p>The shape matrix is returned as a defensive copy to prevent external modification.
 * 
 * @author Rajul Kabir
 * @version 1.0
 * @see com.comp2042.util.BrickRotator
 */
public final class NextShapeInfo {

    /** The shape matrix for this rotation state. */
    private final int[][] shape;
    
    /** The rotation position index (0-3). */
    private final int position;

    /**
     * Creates a new NextShapeInfo object.
     * 
     * @param shape The shape matrix for this rotation state
     * @param position The rotation position index (0-3)
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Gets a copy of the shape matrix for this rotation state.
     * 
     * @return A defensive copy of the shape matrix
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /**
     * Gets the rotation position index.
     * 
     * @return The rotation position (0-3), where 0 is the initial orientation
     */
    public int getPosition() {
        return position;
    }
}
