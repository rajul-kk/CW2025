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
 * Manages the pause menu dialog for the game.
 * Decouples pause menu UI creation and management from the GUI controller.
 */
public class PauseMenuDialog {
    
    
    private Stage pauseMenuStage;
    private final Stage ownerStage;
    
    // Callbacks for menu actions
    private final Runnable onResume;
    private final Runnable onNewGame;
    private final Runnable onShowControls;
    private final Runnable onExit;
    
    /**
     * Creates a new PauseMenuDialog.
     * 
     * @param ownerStage The primary stage that owns this dialog
     * @param onResume Callback when resume is clicked
     * @param onNewGame Callback when new game is clicked
     * @param onShowControls Callback when controls is clicked
     * @param onExit Callback when exit is clicked
     */
    public PauseMenuDialog(Stage ownerStage, Runnable onResume, Runnable onNewGame, 
                          Runnable onShowControls, Runnable onExit) {
        this.ownerStage = ownerStage;
        this.onResume = onResume;
        this.onNewGame = onNewGame;
        this.onShowControls = onShowControls;
        this.onExit = onExit;
    }
    
    /**
     * Shows the pause menu dialog.
     * If already showing, does nothing.
     */
    public void show() {
        if (pauseMenuStage != null && pauseMenuStage.isShowing()) {
            return; // Already showing
        }
        
        VBox pauseMenu = createMenuContent();
        Scene pauseScene = createScene(pauseMenu);
        pauseMenuStage = createStage(pauseScene);
        
        centerMenuOverOwner();
        pauseMenuStage.show();
        pauseMenuStage.requestFocus();
    }
    
    /**
     * Closes the pause menu dialog if it's open.
     */
    public void close() {
        if (pauseMenuStage != null && pauseMenuStage.isShowing()) {
            pauseMenuStage.close();
        }
    }
    
    /**
     * Checks if the pause menu is currently showing.
     * 
     * @return true if showing, false otherwise
     */
    public boolean isShowing() {
        return pauseMenuStage != null && pauseMenuStage.isShowing();
    }
    
    private VBox createMenuContent() {
        VBox pauseMenu = new VBox(20);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setPadding(new Insets(30));
        pauseMenu.getStyleClass().add("pause-menu");
        
        Label pauseTitle = new Label("GAME PAUSED");
        pauseTitle.getStyleClass().add("pause-title");
        
        Button resumeButton = createResumeButton();
        Button pauseNewGameButton = createNewGameButton();
        Button controlsButton = createControlsButton();
        Button pauseExitButton = createExitButton();
        
        pauseMenu.getChildren().addAll(
            pauseTitle, resumeButton, pauseNewGameButton, 
            controlsButton, pauseExitButton
        );
        
        return pauseMenu;
    }
    
    private Button createResumeButton() {
        Button resumeButton = new Button("Resume");
        resumeButton.getStyleClass().add(GameConstants.CSS_PAUSE_MENU_BUTTON);
        resumeButton.setDefaultButton(true);
        resumeButton.setOnAction(e -> {
            close();
            onResume.run();
        });
        return resumeButton;
    }
    
    private Button createNewGameButton() {
        Button pauseNewGameButton = new Button("New Game");
        pauseNewGameButton.getStyleClass().add(GameConstants.CSS_PAUSE_MENU_BUTTON);
        pauseNewGameButton.setOnAction(e -> {
            close();
            onNewGame.run();
        });
        return pauseNewGameButton;
    }
    
    private Button createControlsButton() {
        Button controlsButton = new Button("Controls");
        controlsButton.getStyleClass().add(GameConstants.CSS_PAUSE_MENU_BUTTON);
        controlsButton.setOnAction(e -> {
            close();
            onShowControls.run();
        });
        return controlsButton;
    }
    
    private Button createExitButton() {
        Button pauseExitButton = new Button("Exit");
        pauseExitButton.getStyleClass().add(GameConstants.CSS_PAUSE_MENU_BUTTON);
        pauseExitButton.setOnAction(e -> {
            close();
            onExit.run();
        });
        return pauseExitButton;
    }
    
    private Scene createScene(VBox pauseMenu) {
        Scene pauseScene = new Scene(pauseMenu, GameConstants.PAUSE_MENU_WIDTH, GameConstants.PAUSE_MENU_HEIGHT);
        pauseScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        pauseScene.getStylesheets().add(
            getClass().getClassLoader().getResource("window_style.css").toExternalForm()
        );
        
        // Allow ESC to resume the game from the pause menu
        pauseScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                close();
                onResume.run();
                e.consume();
            }
        });
        
        return pauseScene;
    }
    
    private Stage createStage(Scene pauseScene) {
        Stage stage = new Stage();
        stage.initOwner(ownerStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(pauseScene);
        return stage;
    }
    
    private void centerMenuOverOwner() {
        pauseMenuStage.setX(ownerStage.getX() + (ownerStage.getWidth() - GameConstants.PAUSE_MENU_WIDTH) / 2);
        pauseMenuStage.setY(ownerStage.getY() + (ownerStage.getHeight() - GameConstants.PAUSE_MENU_HEIGHT) / 2);
    }
}

