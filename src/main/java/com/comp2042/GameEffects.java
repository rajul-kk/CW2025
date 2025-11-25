package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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
}

