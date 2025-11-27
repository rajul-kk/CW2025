package com.comp2042.model;

import com.comp2042.util.MatrixOperations;

import java.util.Arrays;

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

