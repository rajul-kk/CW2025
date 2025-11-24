package com.comp2042;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * Manages the display matrix (Rectangle[][]) that represents the game board visually.
 * Handles initialization, updates, and styling of board rectangles.
 */
public class BoardDisplayManager {
    
    private Rectangle[][] displayMatrix;
    private final GridPane gamePanel;
    
    /**
     * Creates a new BoardDisplayManager.
     * 
     * @param gamePanel The GridPane where rectangles will be added
     */
    public BoardDisplayManager(GridPane gamePanel) {
        this.gamePanel = gamePanel;
    }
    
    /**
     * Initializes the display matrix based on the board dimensions.
     * Creates and adds rectangles to the GridPane for each board cell.
     * 
     * @param boardMatrix The game board matrix (used to determine dimensions)
     */
    public void initialize(int[][] boardMatrix) {
        if (boardMatrix == null || boardMatrix.length == 0 || boardMatrix[0].length == 0) {
            return;
        }
        
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }
    }
    
    /**
     * Refreshes the display matrix based on the current board state.
     * Updates the styling of each rectangle to match the board data.
     * 
     * @param board The current game board state
     */
    public void refreshGameBackground(int[][] board) {
        if (displayMatrix == null || board == null) {
            return;
        }
        
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }
    
    /**
     * Sets the visual properties of a rectangle based on the color code.
     * 
     * @param color The color code (0 for transparent, 1-7 for block colors)
     * @param rectangle The rectangle to style
     */
    private void setRectangleData(int color, Rectangle rectangle) {
        if (color == 0) {
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(null);
        } else {
            rectangle.setFill(BlockRenderer.getFillColor(color));
            rectangle.setStroke(BlockRenderer.getBorderColor(color));
            rectangle.setStrokeType(StrokeType.INSIDE);
            rectangle.setStrokeWidth(1.5);
            rectangle.setArcHeight(9);
            rectangle.setArcWidth(9);
        }
    }
    
    /**
     * Gets the display matrix for use in animations or other operations.
     * 
     * @return The Rectangle[][] display matrix, or null if not initialized
     */
    public Rectangle[][] getDisplayMatrix() {
        return displayMatrix;
    }
}

