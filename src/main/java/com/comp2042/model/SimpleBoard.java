package com.comp2042.model;

import com.comp2042.data.ViewData;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.util.BrickRotator;
import com.comp2042.util.ClearRow;
import com.comp2042.util.MatrixOperations;

import java.awt.*;

/**
 * Concrete implementation of the Board interface.
 * 
 * <p>This class manages the game board state, including the board matrix,
 * current falling piece, piece generation, rotation, and row clearing.
 * It uses a {@link RandomBrickGenerator} for fair piece distribution and
 * a {@link BrickRotator} for handling piece rotations.
 * 
 * <p>The board is represented as a 2D integer array where:
 * <ul>
 *   <li>0 represents an empty cell</li>
 *   <li>1-7 represent different colored locked blocks</li>
 * </ul>
 * 
 * <p>The board includes hidden rows at the top (rows 0-1) to allow pieces
 * to spawn above the visible area.
 * 
 * @author Rajul Kabir
 * @version 1.0
 * @see Board
 * @see RandomBrickGenerator
 * @see BrickRotator
 */
public class SimpleBoard implements Board {

    /** The width of the game board in cells. */
    private final int width;
    
    /** The height of the game board in cells (including hidden rows). */
    private final int height;
    
    /** The brick generator for creating new pieces. */
    private final BrickGenerator brickGenerator;
    
    /** The brick rotator for handling piece rotations. */
    private final BrickRotator brickRotator;
    
    /** The current game board matrix (2D array of color codes). */
    private int[][] currentGameMatrix;
    
    /** The current position of the falling piece on the board. */
    private Point currentOffset;
    
    /** The score object for tracking game score. */
    private final Score score;

    /**
     * Creates a new SimpleBoard with the specified dimensions.
     * 
     * <p>Initializes the board with an empty matrix, creates a new
     * RandomBrickGenerator for piece generation, and initializes
     * the score system.
     * 
     * @param width The width of the board in cells
     * @param height The height of the board in cells (including hidden rows)
     */
    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(4, 2);
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getShapeMatrix().get(0));
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }

    @Override
    public int[][] getSecondNextBrickData() {
        return brickGenerator.getSecondNextBrick().getShapeMatrix().get(0);
    }

    @Override
    public int[][] getThirdNextBrickData() {
        return brickGenerator.getThirdNextBrick().getShapeMatrix().get(0);
    }

    @Override
    public boolean setBrick(Brick brick, int rotation) {
        brickRotator.setBrick(brick, rotation);
        currentOffset = new Point(4, 2);
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Gets the current brick that is falling.
     * 
     * <p>This method is used by the hold system to retrieve the current
     * piece before swapping it with a held piece.
     * 
     * @return The current Brick object
     */
    public Brick getCurrentBrick() {
        return brickRotator.getCurrentBrick();
    }

    /**
     * Gets the current rotation state of the falling piece.
     * 
     * <p>This method is used by the hold system to preserve the rotation
     * state when swapping pieces.
     * 
     * @return The current rotation index (0-3)
     */
    public int getCurrentRotation() {
        return brickRotator.getCurrentRotation();
    }
}
