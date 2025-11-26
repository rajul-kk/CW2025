package com.comp2042;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Manages phantom mode functionality.
 * In phantom mode, blocks fade to invisible after locking.
 */
public class PhantomManager {
    
    private boolean phantomMode = false;
    
    /**
     * Sets whether phantom mode is enabled.
     * 
     * @param enabled true to enable phantom mode, false to disable
     */
    public void setPhantomMode(boolean enabled) {
        this.phantomMode = enabled;
    }
    
    /**
     * Checks if phantom mode is enabled.
     * 
     * @return true if phantom mode is enabled, false otherwise
     */
    public boolean isPhantomMode() {
        return phantomMode;
    }
    
    /**
     * Plays a fade transition on a node, fading it from fully visible to invisible.
     * Used in phantom mode to fade locked blocks.
     * 
     * @param node The node to fade
     */
    public void playPhantomFade(Node node) {
        if (node == null || !phantomMode) {
            return;
        }
        
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), node);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
    }
    
    /**
     * Triggers phantom fade on a rectangle if phantom mode is enabled.
     * 
     * @param rectangle The rectangle to fade
     */
    public void fadeLockedBlock(Rectangle rectangle) {
        if (phantomMode && rectangle != null) {
            playPhantomFade(rectangle);
        }
    }
    
    /**
     * Checks if a rectangle should be faded after illumination based on phantom mode.
     * 
     * @param rect The rectangle to check
     * @return true if the rectangle should be faded (phantom mode and it's a locked block)
     */
    public boolean shouldFadeAfterIllumination(Rectangle rect) {
        if (!phantomMode || rect == null) {
            return false;
        }
        
        Paint fill = rect.getFill();
        boolean isLockedBlock = fill != null && fill != Color.TRANSPARENT && 
                              !(fill instanceof Color && ((Color) fill).getOpacity() == 0.0);
        return isLockedBlock;
    }
    
    /**
     * Applies the appropriate post-illumination effect based on phantom mode.
     * 
     * @param rect The rectangle to apply the effect to
     */
    public void applyPostIlluminationEffect(Rectangle rect) {
        if (rect == null) {
            return;
        }
        
        Paint fill = rect.getFill();
        boolean isLockedBlock = fill != null && fill != Color.TRANSPARENT && 
                              !(fill instanceof Color && ((Color) fill).getOpacity() == 0.0);
        
        if (phantomMode) {
            if (isLockedBlock) {
                // In phantom mode, fade locked blocks to invisible
                playPhantomFade(rect);
            } else {
                // Empty space, make it transparent
                rect.setOpacity(0.0);
            }
        } else {
            if (isLockedBlock) {
                // In classic mode, keep locked blocks fully visible
                rect.setOpacity(1.0);
            } else {
                // Empty space, make it transparent
                rect.setOpacity(0.0);
            }
        }
    }
    
    /**
     * Applies phantom fade to a rectangle after flash effect if in phantom mode.
     * 
     * @param rect The rectangle to potentially fade
     */
    public void applyPostFlashEffect(Rectangle rect) {
        if (phantomMode && rect != null) {
            playPhantomFade(rect);
        }
    }
}

