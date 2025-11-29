package com.comp2042.data;

import com.comp2042.util.MatrixOperations;

/**
 * Immutable data class containing information about the current game view state.
 * 
 * <p>This class encapsulates all the data needed to render the current game state,
 * including the falling piece's shape and position, as well as the next piece preview.
 * 
 * <p>All matrix data is returned as defensive copies to prevent external modification
 * of the internal game state.
 * 
 * @author Rajul Kabir
 * @version 1.0
 */
public final class ViewData {

    /** The shape matrix of the current falling piece. */
    private final int[][] brickData;
    
    /** The X coordinate of the current piece on the board. */
    private final int xPosition;
    
    /** The Y coordinate of the current piece on the board. */
    private final int yPosition;
    
    /** The shape matrix of the next piece to appear. */
    private final int[][] nextBrickData;

    /**
     * Creates a new ViewData object.
     * 
     * @param brickData The shape matrix of the current falling piece
     * @param xPosition The X coordinate of the current piece
     * @param yPosition The Y coordinate of the current piece
     * @param nextBrickData The shape matrix of the next piece
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
    }

    /**
     * Gets a copy of the current piece's shape matrix.
     * 
     * @return A defensive copy of the brick shape matrix
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    /**
     * Gets the X position of the current piece.
     * 
     * @return The X coordinate on the board
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Gets the Y position of the current piece.
     * 
     * @return The Y coordinate on the board
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Gets a copy of the next piece's shape matrix.
     * 
     * @return A defensive copy of the next brick shape matrix
     */
    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }
}
