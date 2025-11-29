package com.comp2042.controller;

import com.comp2042.data.DownData;
import com.comp2042.data.EventSource;
import com.comp2042.data.EventType;
import com.comp2042.data.MoveEvent;
import com.comp2042.data.ViewData;
import com.comp2042.model.GameConstants;
import com.comp2042.view.GuiController;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Handles keyboard input for the game.
 * Decouples input handling from the GUI controller.
 */
public class InputHandler implements EventHandler<KeyEvent> {
    
    private final InputEventListener gameController;
    private final GuiController guiController;
    private final Scene scene;
    
    private long lastDropTime = 0;
    
    /**
     * Creates a new InputHandler.
     * 
     * @param scene The scene to listen for key events
     * @param gameController The game controller for game logic
     * @param guiController The GUI controller for UI updates
     */
    public InputHandler(Scene scene, InputEventListener gameController, GuiController guiController) {
        this.scene = scene;
        this.gameController = gameController;
        this.guiController = guiController;
        setupKeyListener();
    }
    
    /**
     * Sets up the key listener on the scene.
     */
    private void setupKeyListener() {
        scene.setOnKeyPressed(this);
    }
    
    @Override
    public void handle(KeyEvent e) {
        KeyCode code = e.getCode();
        boolean gameActive = !guiController.isPaused() && !guiController.isGameOver();
        
        if (gameActive && handleGameActiveKeys(code, e)) {
            return;
        }
        
        handleMenuKeys(code, e);
    }
    
    private boolean handleGameActiveKeys(KeyCode code, KeyEvent e) {
        if (isMovementKey(code)) {
            return handleMovementKey(code, e);
        }
        
        if (code == KeyCode.C) {
            handleHoldKey(e);
            return true;
        }
        
        if (code == KeyCode.SPACE) {
            handleHardDropKey();
            e.consume();
            return true;
        }
        
        return false;
    }
    
    private boolean isMovementKey(KeyCode code) {
        return code == KeyCode.LEFT || code == KeyCode.A ||
               code == KeyCode.RIGHT || code == KeyCode.D ||
               code == KeyCode.UP || code == KeyCode.W ||
               code == KeyCode.DOWN || code == KeyCode.S;
    }
    
    private boolean handleMovementKey(KeyCode code, KeyEvent e) {
        if (isLeftKey(code)) {
            handleLeftMovement(e);
            return true;
        }
        
        if (isRightKey(code)) {
            handleRightMovement(e);
            return true;
        }
        
        if (isUpKey(code)) {
            handleRotateMovement(e);
            return true;
        }
        
        if (isDownKey(code)) {
            handleDownMovement(e);
            return true;
        }
        
        return false;
    }
    
    private boolean isLeftKey(KeyCode code) {
        return code == KeyCode.LEFT || code == KeyCode.A;
    }
    
    private boolean isRightKey(KeyCode code) {
        return code == KeyCode.RIGHT || code == KeyCode.D;
    }
    
    private boolean isUpKey(KeyCode code) {
        return code == KeyCode.UP || code == KeyCode.W;
    }
    
    private boolean isDownKey(KeyCode code) {
        return code == KeyCode.DOWN || code == KeyCode.S;
    }
    
    private void handleLeftMovement(KeyEvent e) {
        ViewData viewData = gameController.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
        guiController.refreshBrick(viewData);
        e.consume();
    }
    
    private void handleRightMovement(KeyEvent e) {
        ViewData viewData = gameController.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
        guiController.refreshBrick(viewData);
        e.consume();
    }
    
    private void handleRotateMovement(KeyEvent e) {
        ViewData viewData = gameController.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));
        guiController.refreshBrick(viewData);
        e.consume();
    }
    
    private void handleDownMovement(KeyEvent e) {
        DownData downData = gameController.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
        if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
            guiController.showScoreNotification(downData.getClearRow());
        }
        guiController.refreshBrick(downData.getViewData());
        e.consume();
    }
    
    private void handleHoldKey(KeyEvent e) {
        ViewData viewData = gameController.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER));
        if (viewData != null) {
            guiController.refreshBrick(viewData);
        }
        e.consume();
    }
    
    private void handleHardDropKey() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDropTime > GameConstants.DROP_COOLDOWN_MS) {
            lastDropTime = currentTime;
            if (gameController instanceof GameController gc) {
                gc.dropInstant();
            }
        }
    }
    
    private void handleMenuKeys(KeyCode code, KeyEvent e) {
        if (code == KeyCode.N) {
            guiController.newGame(null);
        } else if (code == KeyCode.ESCAPE) {
            guiController.togglePause();
            e.consume();
        }
    }
    
    /**
     * Requests focus for keyboard input.
     */
    public void requestFocus() {
        if (scene.getRoot() != null) {
            scene.getRoot().requestFocus();
        }
    }
}

