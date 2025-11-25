package com.comp2042;

import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

/**
 * Handles responsive resizing and layout binding for the game UI.
 * Decouples layout management from the GUI controller.
 */
public class LayoutManager {
    
    private final Scene scene;
    
    /**
     * Creates a new LayoutManager.
     * 
     * @param scene The scene containing the UI elements to manage
     */
    public LayoutManager(Scene scene) {
        this.scene = scene;
    }
    
    /**
     * Binds a pane's preferred width to the scene's width with optional scaling.
     * 
     * @param pane The pane to bind
     * @param scaleFactor Optional scale factor (1.0 for 100%, 0.8 for 80%, etc.)
     *                    If null, binds directly to scene width
     */
    public void bindWidth(Pane pane, Double scaleFactor) {
        if (pane == null || scene == null) {
            return;
        }
        
        if (scaleFactor != null) {
            pane.prefWidthProperty().bind(
                Bindings.multiply(scene.widthProperty(), scaleFactor)
            );
        } else {
            pane.prefWidthProperty().bind(scene.widthProperty());
        }
    }
    
    /**
     * Binds a pane's preferred width to the scene's width.
     * 
     * @param pane The pane to bind
     */
    public void bindWidth(Pane pane) {
        bindWidth(pane, null);
    }
    
    /**
     * Binds a pane's preferred height to the scene's height with optional scaling.
     * 
     * @param pane The pane to bind
     * @param scaleFactor Optional scale factor (1.0 for 100%, 0.8 for 80%, etc.)
     *                    If null, binds directly to scene height
     */
    public void bindHeight(Pane pane, Double scaleFactor) {
        if (pane == null || scene == null) {
            return;
        }
        
        if (scaleFactor != null) {
            pane.prefHeightProperty().bind(
                Bindings.multiply(scene.heightProperty(), scaleFactor)
            );
        } else {
            pane.prefHeightProperty().bind(scene.heightProperty());
        }
    }
    
    /**
     * Binds a pane's preferred height to the scene's height.
     * 
     * @param pane The pane to bind
     */
    public void bindHeight(Pane pane) {
        bindHeight(pane, null);
    }
    
    /**
     * Binds both width and height of a pane to the scene with optional scaling.
     * 
     * @param pane The pane to bind
     * @param widthScale Optional width scale factor
     * @param heightScale Optional height scale factor
     */
    public void bindSize(Pane pane, Double widthScale, Double heightScale) {
        bindWidth(pane, widthScale);
        bindHeight(pane, heightScale);
    }
    
    /**
     * Binds both width and height of a pane to the scene.
     * 
     * @param pane The pane to bind
     */
    public void bindSize(Pane pane) {
        bindSize(pane, null, null);
    }
}

