package com.comp2042.util;

/**
 * Immutable data class containing information about a row clearing operation.
 * 
 * <p>This class encapsulates the results of clearing completed rows from the board,
 * including the number of rows removed, the updated board matrix, and the
 * score bonus earned.
 * 
 * <p>The new matrix is returned as a defensive copy to prevent external modification.
 * 
 * @author Rajul Kabir
 * @version 1.0
 */
public final class ClearRow {

    /** The number of rows that were cleared. */
    private final int linesRemoved;
    
    /** The board matrix after rows have been cleared and blocks shifted down. */
    private final int[][] newMatrix;
    
    /** The score bonus earned for clearing these rows. */
    private final int scoreBonus;

    /**
     * Creates a new ClearRow object.
     * 
     * @param linesRemoved The number of rows that were cleared
     * @param newMatrix The updated board matrix after clearing
     * @param scoreBonus The score bonus earned (calculated as 50 × lines²)
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /**
     * Gets the number of rows that were cleared.
     * 
     * @return The number of completed rows removed (0-4)
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Gets a copy of the updated board matrix.
     * 
     * <p>This matrix represents the board state after rows have been cleared
     * and remaining blocks have been shifted down.
     * 
     * @return A defensive copy of the updated board matrix
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    /**
     * Gets the score bonus earned for clearing these rows.
     * 
     * <p>The bonus is calculated as 50 × (lines cleared)².
     * 
     * @return The score bonus points
     */
    public int getScoreBonus() {
        return scoreBonus;
    }
}
