package com.comp2042;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {


    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private javafx.scene.layout.BorderPane gameBoard;

    @FXML
    private GridPane brickPanel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label highScoreLabel;

    @FXML
    private Label linesLabel;

    @FXML
    private Label levelLabel;

    @FXML
    private VBox gameOverScreen;

    @FXML
    private Label gameOverLabel;

    @FXML
    private Button newGameButton;

    @FXML
    private Button exitButton;

    @FXML
    private Pane nextBlockPane1;

    @FXML
    private Pane nextBlockPane2;

    @FXML
    private Pane nextBlockPane3;

    @FXML
    private Pane holdBlockPane;

    private BoardDisplayManager boardDisplayManager;
    
    private PauseMenuDialog pauseMenuDialog;
    
    private ControlsDialog controlsDialog;

    private InputEventListener eventListener;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    
    private final BlockRenderer blockRenderer = new BlockRenderer();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();
    
    private final GameEffects gameEffects = new GameEffects();

    private java.util.List<javafx.scene.Node> currentFallingBlockNodes = new java.util.ArrayList<>();
    
    private final PhantomManager phantomManager = new PhantomManager();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load font using FontLoader (font is already loaded in Main, but ensure it's available)
        FontLoader.loadFont();
        
        // Apply digital font to score labels using FontLoader
        String fontFamily = FontLoader.getFontFamily();
        if (fontFamily != null && !fontFamily.equals("System")) {
            if (scoreLabel != null) {
                scoreLabel.setFont(FontLoader.getFont(38));
            }
            if (highScoreLabel != null) {
                highScoreLabel.setFont(FontLoader.getFont(38));
            }
            if (linesLabel != null) {
                linesLabel.setFont(FontLoader.getFont(38));
            }
            if (levelLabel != null) {
                levelLabel.setFont(FontLoader.getFont(38));
            }
        }
        
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        if (gameOverScreen != null) {
            gameOverScreen.setVisible(false);
            gameOverScreen.setManaged(false);
        }
    }
    
    /**
     * Checks if the game is currently paused.
     * @return true if paused, false otherwise
     */
    public boolean isPaused() {
        return isPause.get();
    }
    
    /**
     * Checks if the game is over.
     * @return true if game over, false otherwise
     */
    public boolean isGameOver() {
        return isGameOver.get();
    }


    public void initGameView(int[][] boardMatrix, ViewData brick) {
        // Initialize board display manager
        boardDisplayManager = new BoardDisplayManager(gamePanel);
        boardDisplayManager.setGuiController(this);
        boardDisplayManager.initialize(boardMatrix);

        // Initialize ghost block management in BlockRenderer
        blockRenderer.initializeGhostManagement(gamePanel);

        // Initialize falling block
        updateFallingBlock(brick);

        // Initialize pause menu dialog
        if (pauseMenuDialog == null) {
            Stage primaryStage = SceneManager.getStage(gamePanel);
            pauseMenuDialog = new PauseMenuDialog(
                primaryStage,
                this::resumeGame,
                () -> newGame(null),
                () -> showControls(null),
                () -> exitGame(null)
            );
        }

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(GameConstants.GAME_TICK_DURATION_MS),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Animation.INDEFINITE);
        timeLine.play();
    }



    public void refreshBrick(ViewData brick) {
        if (brick == null) {
            // Clear falling block and ghost if no brick data (e.g., game over)
            clearFallingBlock();
            blockRenderer.clearGhost();
            return;
        }
        
        if (isPause.getValue() == Boolean.FALSE) {
            // Remove old rectangles and add new ones at updated positions
            updateFallingBlock(brick);
            
            // Draw ghost piece showing where block will land
            if (eventListener instanceof GameController gameController) {
                int ghostY = gameController.calculateGhostY();
                Block currentBlock = new Block(brick.getBrickData());
                blockRenderer.drawGhost(currentBlock, brick.getxPosition(), ghostY);
            }
        } else {
            // Clear ghost when paused
            blockRenderer.clearGhost();
        }
    }
    
    private void clearFallingBlock() {
        for (javafx.scene.Node node : currentFallingBlockNodes) {
            gamePanel.getChildren().remove(node);
        }
        currentFallingBlockNodes.clear();
    }

    private void updateFallingBlock(ViewData brick) {
        // Remove old falling block rectangles from gamePanel
        for (javafx.scene.Node node : currentFallingBlockNodes) {
            gamePanel.getChildren().remove(node);
        }
        currentFallingBlockNodes.clear();
        
        // Clear ghost when updating falling block
        blockRenderer.clearGhost();
        
        int xPos = brick.getxPosition();
        int yPos = brick.getyPosition();
        int[][] brickData = brick.getBrickData();
        
        // Use BlockRenderer to render the falling block
        blockRenderer.renderToGridPane(brickData, xPos, yPos, gamePanel, 
                                       currentFallingBlockNodes, BlockRenderer.BlockStyle.NORMAL);
    }

    public void refreshGameBackground(int[][] board) {
        // Clear ghost when background refreshes (block locked)
        blockRenderer.clearGhost();
        
        if (boardDisplayManager != null) {
            boardDisplayManager.refreshGameBackground(board);
        }
    }

    /**
     * Handles the automatic down movement from the game timeline.
     * This is called by the game loop, not by user input.
     */
    void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                showScoreNotification(downData.getClearRow());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void showScoreNotification(ClearRow clearRow) {
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            NotificationPanel notificationPanel = new NotificationPanel("+" + clearRow.getScoreBonus());
            
            // Center the notification over the gameboard
            notificationPanel.centerOverGameBoard(gameBoard, groupNotification);
            
            if (groupNotification != null) {
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
        }
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        if (scoreLabel != null && integerProperty != null) {
            scoreLabel.textProperty().bind(integerProperty.asString());
        }
    }

    public void updateHighScore(int score) {
        if (highScoreLabel != null) {
            highScoreLabel.setText(String.valueOf(score));
        }
    }

    public void updateLines(int lines) {
        if (linesLabel != null) {
            linesLabel.setText(String.valueOf(lines));
        }
    }

    public void updateLevel(int level) {
        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(level));
        }
    }

    /**
     * Updates the game speed by changing the drop interval.
     * @param dropInterval The new drop interval in milliseconds
     */
    public void updateGameSpeed(int dropInterval) {
        if (timeLine != null) {
            boolean wasPlaying = timeLine.getStatus() == javafx.animation.Animation.Status.RUNNING;
            timeLine.stop();
            
            timeLine = new Timeline(new KeyFrame(
                    Duration.millis(dropInterval),
                    ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            timeLine.setCycleCount(Animation.INDEFINITE);
            
            if (wasPlaying && !isPause.get()) {
                timeLine.play();
            }
        }
    }

    public void gameOver() {
        timeLine.stop();
        showGameOverScreen();
        closePauseMenu();
        isGameOver.setValue(Boolean.TRUE);
        isPause.setValue(Boolean.FALSE); // Ensure pause is cleared on game over
    }

    private void showGameOverScreen() {
        if (gameOverScreen != null) {
            gameOverScreen.setVisible(true);
            gameOverScreen.setManaged(true);
        }
    }

    @FXML
    public void onNewGameButtonClick(ActionEvent actionEvent) {
        hideGameOverScreen();
        newGame(actionEvent);
    }

    @FXML
    public void onExitButtonClick(ActionEvent actionEvent) {
        returnToMainMenu();
    }

    private void hideGameOverScreen() {
        if (gameOverScreen != null) {
            gameOverScreen.setVisible(false);
            gameOverScreen.setManaged(false);
        }
    }

    @FXML
    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        hideGameOverScreen();
        closePauseMenu();
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    @FXML
    public void pauseGame(ActionEvent actionEvent) {
        togglePause();
    }

    public void togglePause() {
        if (isGameOver.get()) {
            return; // Don't allow pausing when game is over
        }
        
        boolean currentlyPaused = isPause.get();
        
        if (currentlyPaused) {
            // Resume the game
            resumeGame();
        } else {
            // Pause the game
            doPause();
        }
    }
    
    private void doPause() {
        isPause.setValue(Boolean.TRUE);
        if (timeLine != null) {
            timeLine.pause();
        }
        showPauseMenu();
    }
    
    private void resumeGame() {
        isPause.setValue(Boolean.FALSE);
        if (timeLine != null) {
            timeLine.play();
        }
        closePauseMenu();
        gamePanel.requestFocus();
    }
    
    private void showPauseMenu() {
        if (pauseMenuDialog != null) {
            pauseMenuDialog.show();
        }
    }
    
    private void closePauseMenu() {
        if (pauseMenuDialog != null) {
            pauseMenuDialog.close();
        }
    }

    @FXML
    public void showControls(ActionEvent actionEvent) {
        // Check if game is paused before opening controls menu
        boolean wasPaused = isPause.getValue();
        
        // Close existing dialog if showing
        if (controlsDialog != null && controlsDialog.isShowing()) {
            controlsDialog.close();
        }
        
        // Create new dialog with current pause state
        Stage primaryStage = SceneManager.getStage(gamePanel);
        controlsDialog = new ControlsDialog(
            primaryStage,
            () -> {
                if (wasPaused) {
                    resumeGame();
                }
            }
        );
        
        controlsDialog.show();
    }

    @FXML
    public void showAbout(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About TetrisJFX");
        alert.setHeaderText("TetrisJFX");
        alert.setContentText("""
            A classic Tetris game implementation in JavaFX.

            Clear rows by completing horizontal lines.
            Score points by clearing multiple rows at once!

            Version 1.0""");
        alert.showAndWait();
        gamePanel.requestFocus();
    }

    @FXML
    public void exitGame(ActionEvent actionEvent) {
        returnToMainMenu();
    }
    
    private void returnToMainMenu() {
        // Stop the game timeline if running
        if (timeLine != null) {
            timeLine.stop();
        }
        
        // Close pause menu if open
        closePauseMenu();
        
        // Switch to main menu scene using SceneManager
        if (!SceneManager.switchToMainMenu(gamePanel, getClass())) {
            // Fallback to exit if menu loading fails
            SceneManager.handleTransitionError();
        }
    }

    /**
     * Draws a block in the specified pane. Handles centering and styling.
     * @param block The block to draw, or null to clear the pane
     * @param pane The pane to draw in
     */
    private void drawBlockInPane(Block block, Pane pane) {
        if (block == null) {
            if (pane != null) {
                pane.getChildren().clear();
            }
            return;
        }
        
        int[][] shape = block.getShape();
        blockRenderer.renderToPane(shape, pane, BlockRenderer.BlockStyle.PREVIEW);
    }

    public void drawNextBlock1(Block block) {
        drawBlockInPane(block, nextBlockPane1);
    }

    public void drawNextBlock2(Block block) {
        drawBlockInPane(block, nextBlockPane2);
    }

    public void drawNextBlock3(Block block) {
        drawBlockInPane(block, nextBlockPane3);
    }

    public void drawHoldBlock(Block block) {
        drawBlockInPane(block, holdBlockPane);
    }

    /**
     * Animates the lock effect when a block lands and locks into place.
     * Creates a pulse animation by scaling the block's rectangles up and back down.
     * 
     * @param viewData The ViewData containing the block's position and shape data
     */
    public void animateLockBlock(ViewData viewData) {
        if (viewData == null || boardDisplayManager == null) {
            return;
        }

        Rectangle[][] displayMatrix = boardDisplayManager.getDisplayMatrix();
        if (displayMatrix == null) {
            return;
        }

        int[][] brickData = viewData.getBrickData();
        int xPos = viewData.getxPosition();
        int yPos = viewData.getyPosition();

        gameEffects.animateLockBlock(displayMatrix, brickData, xPos, yPos);
    }

    /**
     * Creates a screen shake effect when a hard drop occurs.
     * Shakes the game board randomly by small amounts and returns to center.
     */
    public void shakeBoard() {
        gameEffects.shakeBoard(gameBoard);
    }

    /**
     * Sets whether phantom mode is enabled.
     * In phantom mode, blocks fade to near-invisibility after locking.
     * 
     * @param enabled true to enable phantom mode, false to disable
     */
    public void setPhantomMode(boolean enabled) {
        phantomManager.setPhantomMode(enabled);
    }

    /**
     * Checks if phantom mode is enabled.
     * 
     * @return true if phantom mode is enabled, false otherwise
     */
    public boolean isPhantomMode() {
        return phantomManager.isPhantomMode();
    }
    
    /**
     * Public method to trigger phantom fade on a rectangle.
     * Called from BoardDisplayManager when a block locks.
     * 
     * @param rectangle The rectangle to fade
     */
    public void fadeLockedBlock(Rectangle rectangle) {
        phantomManager.fadeLockedBlock(rectangle);
    }
    
    /**
     * Flashes all visible blocks on the board for 0.2 seconds.
     * Used when rows are cleared to help the player re-orient themselves.
     */
    public void flashAllBlocks() {
        if (boardDisplayManager == null) {
            return;
        }
        
        Rectangle[][] displayMatrix = boardDisplayManager.getDisplayMatrix();
        if (displayMatrix == null) {
            return;
        }
        
        gameEffects.flashAllBlocks(displayMatrix, rect -> phantomManager.applyPostFlashEffect(rect));
    }
    
    /**
     * Illuminates a 2x2 radius around the given position for 0.2 seconds.
     * Used when a block locks to briefly highlight the area.
     * 
     * @param boardRow The row index in the board matrix (0-based, including hidden rows)
     * @param boardCol The column index in the board matrix (0-based)
     */
    public void illuminateArea(int boardRow, int boardCol) {
        if (boardDisplayManager == null) {
            return;
        }
        
        Rectangle[][] displayMatrix = boardDisplayManager.getDisplayMatrix();
        if (displayMatrix == null) {
            return;
        }
        
        gameEffects.illuminateArea(displayMatrix, boardRow, boardCol, 
                                  rect -> phantomManager.applyPostIlluminationEffect(rect));
    }
}
