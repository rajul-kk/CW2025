package com.comp2042.ui;

import com.comp2042.model.GameConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Manages the controls dialog for the game.
 * Decouples controls dialog UI creation and management from the GUI controller.
 */
public class ControlsDialog {
    
    
    private Stage controlsStage;
    private final Stage ownerStage;
    private final Runnable onClose;
    
    /**
     * Creates a new ControlsDialog.
     * 
     * @param ownerStage The primary stage that owns this dialog
     * @param onClose Callback when dialog is closed (e.g., to resume game if it was paused)
     */
    public ControlsDialog(Stage ownerStage, Runnable onClose) {
        this.ownerStage = ownerStage;
        this.onClose = onClose;
    }
    
    /**
     * Shows the controls dialog.
     * If already showing, does nothing.
     */
    public void show() {
        if (controlsStage != null && controlsStage.isShowing()) {
            return; // Already showing
        }
        
        VBox vbox = createDialogContent();
        Scene controlsScene = createScene(vbox);
        controlsStage = createStage(controlsScene);
        
        centerDialogOverOwner();
        controlsStage.show();
        controlsStage.requestFocus();
    }
    
    /**
     * Closes the controls dialog if it's open.
     */
    public void close() {
        if (controlsStage != null && controlsStage.isShowing()) {
            controlsStage.close();
        }
    }
    
    /**
     * Checks if the controls dialog is currently showing.
     * 
     * @return true if showing, false otherwise
     */
    public boolean isShowing() {
        return controlsStage != null && controlsStage.isShowing();
    }
    
    private VBox createDialogContent() {
        VBox vbox = new VBox(6);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(15));
        vbox.getStyleClass().add("controls-dialog");
        
        // Title
        Label titleLabel = new Label("GAME CONTROLS");
        titleLabel.getStyleClass().add("controls-title");
        
        // Section headers
        Label movementHeader = new Label("MOVEMENT:");
        movementHeader.getStyleClass().add(GameConstants.CSS_CONTROLS_SECTION_HEADER);
        
        Label gameHeader = new Label("GAME CONTROLS:");
        gameHeader.getStyleClass().add(GameConstants.CSS_CONTROLS_SECTION_HEADER);
        
        Label menuHeader = new Label("MENU SHORTCUTS:");
        menuHeader.getStyleClass().add(GameConstants.CSS_CONTROLS_SECTION_HEADER);
        
        // Movement controls
        Label leftControl = new Label("  Left Arrow / A  →  Move brick left");
        leftControl.getStyleClass().add(GameConstants.CSS_CONTROLS_ITEM);
        Label rightControl = new Label("  Right Arrow / D →  Move brick right");
        rightControl.getStyleClass().add(GameConstants.CSS_CONTROLS_ITEM);
        Label downControl = new Label("  Down Arrow / S  →  Move brick down (faster)");
        downControl.getStyleClass().add(GameConstants.CSS_CONTROLS_ITEM);
        Label hardDropControl = new Label("  Spacebar        →  Hard drop (instant drop)");
        hardDropControl.getStyleClass().add(GameConstants.CSS_CONTROLS_ITEM);
        Label rotateControl = new Label("  Up Arrow / W    →  Rotate brick");
        rotateControl.getStyleClass().add(GameConstants.CSS_CONTROLS_ITEM);
        Label holdControl = new Label("  C                →  Hold/swap brick");
        holdControl.getStyleClass().add(GameConstants.CSS_CONTROLS_ITEM);
        
        // Game controls
        Label pauseControl = new Label("  ESC              →  Pause/Resume game");
        pauseControl.getStyleClass().add(GameConstants.CSS_CONTROLS_ITEM);
        Label newGameControl = new Label("  N               →  New game");
        newGameControl.getStyleClass().add(GameConstants.CSS_CONTROLS_ITEM);
        
        // Menu shortcuts
        Label newGameShortcut = new Label("  Ctrl+N          →  New game");
        newGameShortcut.getStyleClass().add(GameConstants.CSS_CONTROLS_ITEM);
        Label exitShortcut = new Label("  Ctrl+Q          →  Exit game");
        exitShortcut.getStyleClass().add(GameConstants.CSS_CONTROLS_ITEM);
        
        // Close button
        Button closeButton = createCloseButton();
        
        vbox.getChildren().addAll(
            titleLabel,
            movementHeader,
            leftControl, rightControl, downControl, hardDropControl, rotateControl, holdControl,
            gameHeader,
            pauseControl, newGameControl,
            menuHeader,
            newGameShortcut, exitShortcut,
            new Label(""), // Spacer
            closeButton
        );
        
        return vbox;
    }
    
    private Button createCloseButton() {
        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("controls-close-button");
        closeButton.setOnAction(e -> {
            close();
            if (onClose != null) {
                onClose.run();
            }
        });
        return closeButton;
    }
    
    private Scene createScene(VBox vbox) {
        Scene controlsScene = new Scene(vbox, GameConstants.CONTROLS_DIALOG_WIDTH, GameConstants.CONTROLS_DIALOG_HEIGHT);
        controlsScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        controlsScene.getStylesheets().add(
            getClass().getClassLoader().getResource("window_style.css").toExternalForm()
        );
        
        // Allow ESC to close
        controlsScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                close();
                if (onClose != null) {
                    onClose.run();
                }
                e.consume();
            }
        });
        
        return controlsScene;
    }
    
    private Stage createStage(Scene controlsScene) {
        Stage stage = new Stage();
        stage.initOwner(ownerStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Game Controls");
        stage.setScene(controlsScene);
        
        // Handle window close event (X button)
        stage.setOnCloseRequest(e -> {
            if (onClose != null) {
                onClose.run();
            }
        });
        
        return stage;
    }
    
    private void centerDialogOverOwner() {
        controlsStage.setX(ownerStage.getX() + (ownerStage.getWidth() - GameConstants.CONTROLS_DIALOG_WIDTH) / 2);
        controlsStage.setY(ownerStage.getY() + (ownerStage.getHeight() - GameConstants.CONTROLS_DIALOG_HEIGHT) / 2);
    }
}

