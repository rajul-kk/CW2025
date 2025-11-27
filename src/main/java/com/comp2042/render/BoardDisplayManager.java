package com.comp2042.render;

import com.comp2042.model.GameConstants;
import com.comp2042.view.GuiController;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the display matrix (Rectangle[][]) that represents the game board visually.
 * Handles initialization, updates, and styling of board rectangles.
 */
public class BoardDisplayManager {
    
    private Rectangle[][] displayMatrix;
    private final GridPane gamePanel;
    private GuiController guiController;
    
    /**
     * Creates a new BoardDisplayManager.
     * 
     * @param gamePanel The GridPane where rectangles will be added
     */
    public BoardDisplayManager(GridPane gamePanel) {
        this.gamePanel = gamePanel;
    }
    
    /**
     * Sets the GuiController reference for phantom mode fade effects.
     * 
     * @param guiController The GuiController instance
     */
    public void setGuiController(GuiController guiController) {
        this.guiController = guiController;
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
                // Add grid lines to show each block position
                rectangle.setStroke(Color.rgb(100, 100, 100, 0.3)); // Semi-transparent gray grid lines
                rectangle.setStrokeType(StrokeType.INSIDE);
                rectangle.setStrokeWidth(0.5);
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
        
        // Collect newly locked block positions for illumination
        List<int[]> newlyLockedBlocks = new ArrayList<>();
        
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Rectangle rectangle = displayMatrix[i][j];
                // Check if rectangle is transitioning from transparent to colored (block just locked)
                Paint currentFill = rectangle.getFill();
                boolean wasTransparent = currentFill == null || 
                                        currentFill == Color.TRANSPARENT ||
                                        (currentFill instanceof Color && ((Color) currentFill).getOpacity() == 0.0);
                boolean isNowColored = board[i][j] != 0;
                
                setRectangleData(board[i][j], rectangle);
                
                // If a block just locked (transparent -> colored)
                if (wasTransparent && isNowColored) {
                    // Store the position for illumination
                    newlyLockedBlocks.add(new int[]{i, j});
                    
                    // If phantom mode is enabled, fade it after illumination
                    if (guiController != null) {
                        // Ensure the rectangle starts fully visible before fading
                        rectangle.setOpacity(1.0);
                    }
                }
            }
        }
        
        // Illuminate areas around newly locked blocks
        if (guiController != null && !newlyLockedBlocks.isEmpty()) {
            for (int[] position : newlyLockedBlocks) {
                guiController.illuminateArea(position[0], position[1]);
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
            // Keep grid lines visible even when cell is empty
            rectangle.setStroke(Color.rgb(100, 100, 100, 0.3));
            rectangle.setStrokeType(StrokeType.INSIDE);
            rectangle.setStrokeWidth(0.5);
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

