package com.comp2042.render;

import com.comp2042.model.GameConstants;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Handles visual effects and animations for the game UI.
 * Separates animation logic from the main controller to improve code organization.
 */
public class GameEffects {
    
    
    /**
     * Animates a lock pulse effect on a rectangle.
     * Creates a scale transition that pulses the rectangle up and back down.
     * 
     * @param rectangle The rectangle to animate
     */
    public void animateLockPulse(Rectangle rectangle) {
        if (rectangle == null) {
            return;
        }
        
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(GameConstants.LOCK_PULSE_DURATION_MS), rectangle);
        scaleTransition.setFromX(GameConstants.LOCK_PULSE_SCALE_FROM);
        scaleTransition.setFromY(GameConstants.LOCK_PULSE_SCALE_FROM);
        scaleTransition.setToX(GameConstants.LOCK_PULSE_SCALE_TO);
        scaleTransition.setToY(GameConstants.LOCK_PULSE_SCALE_TO);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(GameConstants.LOCK_PULSE_CYCLES);
        scaleTransition.play();
    }
    
    /**
     * Animates a lock pulse effect on multiple rectangles that form a block.
     * 
     * @param displayMatrix The matrix of rectangles representing the game board
     * @param brickData The block shape data
     * @param xPos The X position of the block
     * @param yPos The Y position of the block
     */
    public void animateLockBlock(Rectangle[][] displayMatrix, int[][] brickData, int xPos, int yPos) {
        if (displayMatrix == null || brickData == null) {
            return;
        }
        
        // Animate each rectangle that is part of the locked block
        // Note: brickData is indexed as [column][row] based on merge function
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[j][i] != 0) {
                    animateRectangleAtPosition(displayMatrix, xPos, yPos, i, j);
                }
            }
        }
    }
    
    private void animateRectangleAtPosition(Rectangle[][] displayMatrix, int xPos, int yPos, int i, int j) {
        int boardCol = xPos + i;  // i maps to column (X)
        int boardRow = yPos + j;  // j maps to row (Y)
        
        if (isValidBoardPosition(displayMatrix, boardRow, boardCol)) {
            Rectangle rectangle = displayMatrix[boardRow][boardCol];
            if (rectangle != null) {
                animateLockPulse(rectangle);
            }
        }
    }
    
    private boolean isValidBoardPosition(Rectangle[][] displayMatrix, int boardRow, int boardCol) {
        return boardRow >= 2 && boardRow < displayMatrix.length && 
               boardCol >= 0 && boardCol < displayMatrix[boardRow].length;
    }
    
    /**
     * Creates a screen shake effect on the game board.
     * Shakes the board randomly by small amounts and returns to center.
     * 
     * @param gameBoard The BorderPane representing the game board to shake
     */
    public void shakeBoard(BorderPane gameBoard) {
        if (gameBoard == null) {
            return;
        }
        
        Timeline shakeTimeline = new Timeline();
        
        Duration totalDuration = Duration.millis(GameConstants.SHAKE_DURATION_MS);
        Duration frameDuration = Duration.millis(totalDuration.toMillis() / GameConstants.SHAKE_COUNT);
        
        // Store original position
        double originalX = gameBoard.getTranslateX();
        double originalY = gameBoard.getTranslateY();
        
        // Create keyframes for shake animation
        for (int i = 0; i <= GameConstants.SHAKE_COUNT; i++) {
            final int frame = i;
            KeyFrame keyFrame = new KeyFrame(
                frameDuration.multiply(i),
                e -> {
                    if (frame == GameConstants.SHAKE_COUNT) {
                        // Last frame: return to original position
                        gameBoard.setTranslateX(originalX);
                        gameBoard.setTranslateY(originalY);
                    } else {
                        // Random shake offset
                        double offsetX = (Math.random() * 2 - 1) * GameConstants.SHAKE_MAX_OFFSET;
                        double offsetY = (Math.random() * 2 - 1) * GameConstants.SHAKE_MAX_OFFSET;
                        gameBoard.setTranslateX(originalX + offsetX);
                        gameBoard.setTranslateY(originalY + offsetY);
                    }
                }
            );
            shakeTimeline.getKeyFrames().add(keyFrame);
        }
        
        shakeTimeline.play();
    }
    
    /**
     * Flashes all visible blocks on the board for 0.2 seconds.
     * Used when rows are cleared to help the player re-orient themselves.
     * 
     * @param displayMatrix The matrix of rectangles representing the game board
     * @param postFlashEffect Callback to apply after flash completes (e.g., phantom mode fade)
     */
    public void flashAllBlocks(Rectangle[][] displayMatrix, Consumer<Rectangle> postFlashEffect) {
        if (displayMatrix == null) {
            return;
        }
        
        List<Rectangle> visibleBlocks = collectVisibleBlocks(displayMatrix);
        
        if (visibleBlocks.isEmpty()) {
            return;
        }
        
        // Flash effect: quickly fade out and back in
        // First, make all blocks fully visible
        for (Rectangle rect : visibleBlocks) {
            rect.setOpacity(1.0);
        }
        
        // Create fade out transitions for all blocks
        for (Rectangle rect : visibleBlocks) {
            createFlashAnimation(rect, postFlashEffect);
        }
    }
    
    /**
     * Collects all visible (non-transparent) blocks from the display matrix.
     * 
     * @param displayMatrix The matrix of rectangles representing the game board
     * @return List of visible rectangles
     */
    private List<Rectangle> collectVisibleBlocks(Rectangle[][] displayMatrix) {
        List<Rectangle> visibleBlocks = new ArrayList<>();
        
        for (int i = 2; i < displayMatrix.length; i++) {
            for (int j = 0; j < displayMatrix[i].length; j++) {
                Rectangle rect = displayMatrix[i][j];
                if (rect != null && isBlockVisible(rect)) {
                    visibleBlocks.add(rect);
                }
            }
        }
        
        return visibleBlocks;
    }
    
    /**
     * Checks if a rectangle represents a visible block (non-transparent).
     * 
     * @param rect The rectangle to check
     * @return true if the block is visible, false otherwise
     */
    private boolean isBlockVisible(Rectangle rect) {
        Paint fill = rect.getFill();
        return fill != null && fill != Color.TRANSPARENT && 
               !(fill instanceof Color && ((Color) fill).getOpacity() == 0.0);
    }
    
    /**
     * Creates a flash animation (fade out then fade in) for a rectangle.
     * 
     * @param rect The rectangle to animate
     * @param postFlashEffect Callback to apply after flash completes
     */
    private void createFlashAnimation(Rectangle rect, Consumer<Rectangle> postFlashEffect) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), rect);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.3);
        fadeOut.setOnFinished(e -> createFadeInAnimation(rect, postFlashEffect));
        fadeOut.play();
    }
    
    /**
     * Creates a fade-in animation and applies post-flash effect when complete.
     * 
     * @param rect The rectangle to animate
     * @param postFlashEffect Callback to apply after flash completes
     */
    private void createFadeInAnimation(Rectangle rect, Consumer<Rectangle> postFlashEffect) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(100), rect);
        fadeIn.setFromValue(0.3);
        fadeIn.setToValue(1.0);
        fadeIn.setOnFinished(e2 -> {
            if (postFlashEffect != null) {
                postFlashEffect.accept(rect);
            }
        });
        fadeIn.play();
    }
    
    /**
     * Illuminates a 2x2 radius around the given position for 0.2 seconds.
     * Used when a block locks to briefly highlight the area.
     * 
     * @param displayMatrix The matrix of rectangles representing the game board
     * @param boardRow The row index in the board matrix (0-based, including hidden rows)
     * @param boardCol The column index in the board matrix (0-based)
     * @param postIlluminationEffect Callback to apply after illumination completes (e.g., phantom mode fade)
     */
    public void illuminateArea(Rectangle[][] displayMatrix, int boardRow, int boardCol, 
                              Consumer<Rectangle> postIlluminationEffect) {
        if (displayMatrix == null) {
            return;
        }
        
        List<Rectangle> illuminatedRectangles = collectIlluminatedRectangles(displayMatrix, boardRow, boardCol);
        
        if (!illuminatedRectangles.isEmpty()) {
            schedulePostIlluminationEffect(illuminatedRectangles, postIlluminationEffect);
        }
    }
    
    /**
     * Collects all rectangles within the illumination radius around the given position.
     * 
     * @param displayMatrix The matrix of rectangles representing the game board
     * @param boardRow The row index in the board matrix
     * @param boardCol The column index in the board matrix
     * @return List of illuminated rectangles
     */
    private List<Rectangle> collectIlluminatedRectangles(Rectangle[][] displayMatrix, int boardRow, int boardCol) {
        List<Rectangle> illuminatedRectangles = new ArrayList<>();
        int radius = 2;
        
        for (int rowOffset = -radius; rowOffset <= radius; rowOffset++) {
            for (int colOffset = -radius; colOffset <= radius; colOffset++) {
                int targetRow = boardRow + rowOffset;
                int targetCol = boardCol + colOffset;
                
                Rectangle rect = getRectangleAtPosition(displayMatrix, targetRow, targetCol);
                if (rect != null) {
                    rect.setOpacity(1.0);
                    illuminatedRectangles.add(rect);
                }
            }
        }
        
        return illuminatedRectangles;
    }
    
    /**
     * Gets a rectangle from the display matrix at the specified position if it's within bounds.
     * 
     * @param displayMatrix The matrix of rectangles representing the game board
     * @param targetRow The target row index
     * @param targetCol The target column index
     * @return The rectangle at the position, or null if out of bounds
     */
    private Rectangle getRectangleAtPosition(Rectangle[][] displayMatrix, int targetRow, int targetCol) {
        if (isValidIlluminationPosition(displayMatrix, targetRow, targetCol)) {
            return displayMatrix[targetRow][targetCol];
        }
        return null;
    }
    
    /**
     * Checks if a position is valid for illumination.
     * 
     * @param displayMatrix The matrix of rectangles representing the game board
     * @param targetRow The target row index
     * @param targetCol The target column index
     * @return true if the position is valid, false otherwise
     */
    private boolean isValidIlluminationPosition(Rectangle[][] displayMatrix, int targetRow, int targetCol) {
        return targetRow >= 2 && targetRow < displayMatrix.length &&
               targetCol >= 0 && targetCol < displayMatrix[targetRow].length;
    }
    
    /**
     * Schedules the post-illumination effect to be applied after a delay.
     * 
     * @param illuminatedRectangles The rectangles that were illuminated
     * @param postIlluminationEffect Callback to apply after illumination completes
     */
    private void schedulePostIlluminationEffect(List<Rectangle> illuminatedRectangles, 
                                                Consumer<Rectangle> postIlluminationEffect) {
        PauseTransition pause = new PauseTransition(Duration.millis(200));
        pause.setOnFinished(e -> applyPostIlluminationEffects(illuminatedRectangles, postIlluminationEffect));
        pause.play();
    }
    
    /**
     * Applies post-illumination effects to all illuminated rectangles.
     * 
     * @param illuminatedRectangles The rectangles to apply effects to
     * @param postIlluminationEffect Callback to apply after illumination completes
     */
    private void applyPostIlluminationEffects(List<Rectangle> illuminatedRectangles, 
                                              Consumer<Rectangle> postIlluminationEffect) {
        if (postIlluminationEffect == null) {
            return;
        }
        
        for (Rectangle rect : illuminatedRectangles) {
            postIlluminationEffect.accept(rect);
        }
    }
}

