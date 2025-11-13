package com.comp2042;

public final class Block {

    private final int[][] shape;

    public Block(int[][] shape) {
        this.shape = MatrixOperations.copy(shape);
    }

    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }
}

