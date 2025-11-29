package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Interface representing a Tetris brick (piece) with multiple rotation states.
 * 
 * <p>Each brick can have multiple rotation states, typically 4 (0°, 90°, 180°, 270°).
 * The shape matrix is returned as a defensive copy to prevent external modification.
 * 
 * <p>Implementations of this interface represent the seven standard Tetris pieces:
 * I, J, L, O, S, T, and Z.
 * 
 * @author Rajul Kabir
 * @version 1.0
 * @see BrickGenerator
 */
public interface Brick {

    /**
     * Gets all rotation states of this brick.
     * 
     * <p>Returns a list of 2D arrays, where each array represents the brick's
     * shape at a different rotation state. The list typically contains 4 elements
     * for the four possible rotations.
     * 
     * <p>The returned matrices are defensive copies to prevent external modification.
     * 
     * @return A list of shape matrices, one for each rotation state
     */
    List<int[][]> getShapeMatrix();
}
