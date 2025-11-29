package com.comp2042.model;

import com.comp2042.util.MatrixOperations;

import java.util.Arrays;

/**
 * Immutable record representing a Tetris block shape.
 * 
 * <p>This record encapsulates a block's shape as a 2D integer array.
 * The shape matrix is stored as a defensive copy to ensure immutability.
 * 
 * <p>The shape matrix uses the following encoding:
 * <ul>
 *   <li>0 represents an empty cell</li>
 *   <li>1-7 represent different colored block cells</li>
 * </ul>
 * 
 * <p>This class provides proper equality and hashing based on the
 * shape matrix contents, making it suitable for use in collections.
 * 
 * @param shape The 2D array representing the block's shape
 * 
 * @author Rajul Kabir
 * @version 1.0
 */
public record Block(int[][] shape) {
    
    public Block {
        shape = MatrixOperations.copy(shape);
    }
    
    @Override
    public int[][] shape() {
        return MatrixOperations.copy(shape);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Block block = (Block) obj;
        return Arrays.deepEquals(shape, block.shape);
    }
    
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(shape);
    }
    
    @Override
    public String toString() {
        return "Block" + Arrays.deepToString(shape);
    }
}

