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
        
        List<int[]> newlyLockedBlocks = processBoardUpdates(board);
        illuminateNewlyLockedBlocks(newlyLockedBlocks);
    }
    
    /**
     * Processes board updates and collects positions of newly locked blocks.
     * 
     * @param board The current game board state
     * @return List of positions where blocks were newly locked
     */
    private List<int[]> processBoardUpdates(int[][] board) {
        List<int[]> newlyLockedBlocks = new ArrayList<>();
        
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Rectangle rectangle = displayMatrix[i][j];
                
                // Check if block is newly locked BEFORE updating the rectangle
                // (we need to check the old fill state before setRectangleData changes it)
                boolean isNewlyLocked = isNewlyLockedBlock(rectangle, board[i][j]);
                
                // Update the rectangle's visual properties
                setRectangleData(board[i][j], rectangle);
                
                if (isNewlyLocked) {
                    newlyLockedBlocks.add(new int[]{i, j});
                    prepareRectangleForIllumination(rectangle);
                }
            }
        }
        
        return newlyLockedBlocks;
    }
    
    /**
     * Checks if a block was newly locked (transitioned from transparent to colored).
     * 
     * @param rectangle The rectangle to check
     * @param boardValue The current board value at this position
     * @return true if the block was newly locked, false otherwise
     */
    private boolean isNewlyLockedBlock(Rectangle rectangle, int boardValue) {
        boolean wasTransparent = isTransparent(rectangle.getFill());
        boolean isNowColored = boardValue != 0;
        return wasTransparent && isNowColored;
    }
    
    /**
     * Checks if a paint represents a transparent fill.
     * 
     * @param fill The paint to check
     * @return true if the fill is transparent, false otherwise
     */
    private boolean isTransparent(Paint fill) {
        return fill == null || 
               fill == Color.TRANSPARENT ||
               (fill instanceof Color && ((Color) fill).getOpacity() == 0.0);
    }
    
    /**
     * Prepares a rectangle for illumination effects (e.g., phantom mode fade).
     * 
     * @param rectangle The rectangle to prepare
     */
    private void prepareRectangleForIllumination(Rectangle rectangle) {
        if (guiController != null) {
            rectangle.setOpacity(1.0);
        }
    }
    
    /**
     * Illuminates areas around newly locked blocks.
     * 
     * @param newlyLockedBlocks List of positions where blocks were newly locked
     */
    private void illuminateNewlyLockedBlocks(List<int[]> newlyLockedBlocks) {
        if (guiController == null || newlyLockedBlocks.isEmpty()) {
            return;
        }
        
        for (int[] position : newlyLockedBlocks) {
            guiController.illuminateArea(position[0], position[1]);
        }
    }
    
    /**
     * Sets the visual properties of a rectangle based on the color code.
     * Preserves opacity for already-faded blocks in phantom mode.
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
            // Ensure empty cells remain fully visible so grid lines are always shown
            rectangle.setOpacity(1.0);
        } else {
            // Preserve opacity if block is already faded in phantom mode
            // Only set opacity to 1.0 if it's not already faded (opacity > 0.1)
            boolean isPhantomMode = guiController != null && guiController.isPhantomMode();
            boolean isAlreadyFaded = rectangle.getOpacity() < 0.1;
            
            rectangle.setFill(BlockRenderer.getFillColor(color));
            rectangle.setStroke(BlockRenderer.getBorderColor(color));
            rectangle.setStrokeType(StrokeType.INSIDE);
            rectangle.setStrokeWidth(1.5);
            rectangle.setArcHeight(9);
            rectangle.setArcWidth(9);
            
            // Only reset opacity if not in phantom mode or if block hasn't been faded yet
            if (!isPhantomMode || !isAlreadyFaded) {
                rectangle.setOpacity(1.0);
            }
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

