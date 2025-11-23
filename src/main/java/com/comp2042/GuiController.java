package com.comp2042;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final String BUTTON_STYLE = "-fx-font-size: 16px; -fx-pref-width: 150px; -fx-pref-height: 35px;";

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

    private Rectangle[][] displayMatrix;
    
    private Stage pauseMenuStage;

    private InputEventListener eventListener;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    
    private final BlockRenderer blockRenderer = new BlockRenderer();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();
    
    private final GameEffects gameEffects = new GameEffects();

    private java.util.List<javafx.scene.Node> currentFallingBlockNodes = new java.util.ArrayList<>();
    private java.util.List<javafx.scene.Node> currentGhostNodes = new java.util.ArrayList<>();
    
    private long lastDropTime = 0;
    private static final long DROP_COOLDOWN = 300; // milliseconds

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(this::handleKeyPressed);
        if (gameOverScreen != null) {
            gameOverScreen.setVisible(false);
            gameOverScreen.setManaged(false);
        }
    }

    private void handleKeyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        boolean gameActive = !isPause.getValue() && !isGameOver.getValue();
        
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
        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
        e.consume();
    }

    private void handleRightMovement(KeyEvent e) {
        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
        e.consume();
    }

    private void handleRotateMovement(KeyEvent e) {
        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
        e.consume();
    }

    private void handleDownMovement(KeyEvent e) {
        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
        e.consume();
    }

    private void handleHoldKey(KeyEvent e) {
        ViewData viewData = eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER));
        if (viewData != null) {
            refreshBrick(viewData);
        }
        e.consume();
    }

    private void handleHardDropKey() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDropTime > DROP_COOLDOWN) {
            lastDropTime = currentTime;
            if (eventListener instanceof GameController gameController) {
                gameController.dropInstant();
            }
        }
    }

    private void handleMenuKeys(KeyCode code, KeyEvent e) {
        if (code == KeyCode.N) {
            newGame(null);
        } else if (code == KeyCode.ESCAPE) {
            togglePause();
            e.consume();
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        // Initialize falling block
        updateFallingBlock(brick);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Animation.INDEFINITE);
        timeLine.play();
    }

    private Paint getFillColor(int i) {
        return BlockRenderer.getFillColor(i);
    }
    
    private Color getBorderColor(int i) {
        return BlockRenderer.getBorderColor(i);
    }


    public void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            // Remove old rectangles and add new ones at updated positions
            updateFallingBlock(brick);
            
            // Draw ghost piece showing where block will land
            if (eventListener instanceof GameController gameController) {
                int ghostY = gameController.calculateGhostY();
                Block currentBlock = new Block(brick.getBrickData());
                drawGhost(currentBlock, brick.getxPosition(), ghostY);
            }
        } else {
            // Clear ghost when paused
            clearGhost();
        }
    }

    private void updateFallingBlock(ViewData brick) {
        // Remove old falling block rectangles from gamePanel
        for (javafx.scene.Node node : currentFallingBlockNodes) {
            gamePanel.getChildren().remove(node);
        }
        currentFallingBlockNodes.clear();
        
        // Clear ghost when updating falling block
        clearGhost();
        
        int xPos = brick.getxPosition();
        int yPos = brick.getyPosition();
        int[][] brickData = brick.getBrickData();
        
        // Use BlockRenderer to render the falling block
        blockRenderer.renderToGridPane(brickData, xPos, yPos, gamePanel, 
                                       currentFallingBlockNodes, BlockRenderer.BlockStyle.NORMAL);
    }

    /**
     * Draws a ghost piece (outline with dotted border) showing where the block will land.
     * @param block The block shape to draw
     * @param xPos The X position of the block
     * @param ghostY The Y position where the ghost should appear (calculated landing position)
     */
    public void drawGhost(Block block, int xPos, int ghostY) {
        // Remove old ghost nodes
        clearGhost();
        
        if (block == null) {
            return;
        }
        
        int[][] shape = block.getShape();
        
        // Use BlockRenderer to render the ghost block
        blockRenderer.renderToGridPane(shape, xPos, ghostY, gamePanel, 
                                      currentGhostNodes, BlockRenderer.BlockStyle.GHOST);
    }

    /**
     * Clears the ghost piece from the display.
     */
    private void clearGhost() {
        for (javafx.scene.Node node : currentGhostNodes) {
            gamePanel.getChildren().remove(node);
        }
        currentGhostNodes.clear();
    }

    public void refreshGameBackground(int[][] board) {
        // Clear ghost when background refreshes (block locked)
        clearGhost();
        
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        if (color == 0) {
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(null);
        } else {
            rectangle.setFill(getFillColor(color));
            rectangle.setStroke(getBorderColor(color));
            rectangle.setStrokeType(StrokeType.INSIDE);
            rectangle.setStrokeWidth(1.5);
            rectangle.setArcHeight(9);
            rectangle.setArcWidth(9);
        }
    }

    private void moveDown(MoveEvent event) {
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
            if (gameBoard != null && groupNotification != null && groupNotification.getScene() != null) {
                // Get the gameboard's bounds in the scene
                javafx.geometry.Bounds gameBoardBounds = gameBoard.localToScene(gameBoard.getBoundsInLocal());
                // Calculate center position in scene coordinates
                double centerXScene = gameBoardBounds.getMinX() + gameBoardBounds.getWidth() / 2 - notificationPanel.getMinWidth() / 2;
                double centerYScene = gameBoardBounds.getMinY() + gameBoardBounds.getHeight() / 2 - notificationPanel.getMinHeight() / 2;
                // Convert scene coordinates to groupNotification's local coordinates
                javafx.geometry.Point2D localPoint = groupNotification.sceneToLocal(centerXScene, centerYScene);
                notificationPanel.setLayoutX(localPoint.getX());
                notificationPanel.setLayoutY(localPoint.getY());
            }
            
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
        if (pauseMenuStage != null && pauseMenuStage.isShowing()) {
            return; // Already showing
        }
        
        Stage primaryStage = (Stage) gamePanel.getScene().getWindow();
        
        VBox pauseMenu = new VBox(20);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setPadding(new Insets(30));
        pauseMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-background-radius: 10;");
        
        Label pauseTitle = new Label("GAME PAUSED");
        pauseTitle.setStyle("-fx-font-family: 'Let\'s go Digital'; -fx-font-size: 36px; -fx-text-fill: yellow; -fx-font-weight: bold;");
        
        Button resumeButton = new Button("Resume");
        resumeButton.setStyle(BUTTON_STYLE);
        resumeButton.setDefaultButton(true);
        resumeButton.setOnAction(e -> resumeGame());
        
        Button pauseNewGameButton = new Button("New Game");
        pauseNewGameButton.setStyle(BUTTON_STYLE);
        pauseNewGameButton.setOnAction(e -> {
            closePauseMenu();
            newGame(null);
        });
        
        Button controlsButton = new Button("Controls");
        controlsButton.setStyle(BUTTON_STYLE);
        controlsButton.setOnAction(e -> {
            closePauseMenu();
            showControls(null);
        });
        
        Button pauseExitButton = new Button("Exit");
        pauseExitButton.setStyle(BUTTON_STYLE);
        pauseExitButton.setOnAction(e -> exitGame(null));
        
        pauseMenu.getChildren().addAll(pauseTitle, resumeButton, pauseNewGameButton, controlsButton, pauseExitButton);
        
        Scene pauseScene = new Scene(pauseMenu, 250, 350);
        pauseScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        
        // Allow ESC to resume the game from the pause menu
        pauseScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                resumeGame();
                e.consume();
            }
        });
        
        pauseMenuStage = new Stage();
        pauseMenuStage.initOwner(primaryStage);
        pauseMenuStage.initModality(Modality.WINDOW_MODAL);
        pauseMenuStage.initStyle(StageStyle.TRANSPARENT);
        pauseMenuStage.setScene(pauseScene);
        
        // Center the pause menu over the game window
        pauseMenuStage.setX(primaryStage.getX() + (primaryStage.getWidth() - 250) / 2);
        pauseMenuStage.setY(primaryStage.getY() + (primaryStage.getHeight() - 350) / 2);
        
        pauseMenuStage.show();
        pauseMenuStage.requestFocus();
    }
    
    private void closePauseMenu() {
        if (pauseMenuStage != null && pauseMenuStage.isShowing()) {
            pauseMenuStage.close();
        }
    }

    @FXML
    public void showControls(ActionEvent actionEvent) {
        Stage primaryStage = (Stage) gamePanel.getScene().getWindow();
        
        VBox vbox = new VBox(8);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.95); -fx-background-radius: 10;");
        
        // Title
        Label titleLabel = new Label("GAME CONTROLS");
        titleLabel.setStyle(
            "-fx-font-family: 'Let\'s go Digital'; " +
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.8), 3, 0.0, 1, 1); " +
            "-fx-padding: 0 0 10 0;"
        );
        titleLabel.setTextFill(Color.YELLOW);
        
        // Section headers
        Color sectionHeaderColor = Color.rgb(97, 162, 177); // #61a2b1
        Label movementHeader = new Label("MOVEMENT:");
        movementHeader.setStyle(
            "-fx-font-family: 'Let\'s go Digital'; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 0 4 0;"
        );
        movementHeader.setTextFill(sectionHeaderColor);
        
        Label gameHeader = new Label("GAME CONTROLS:");
        gameHeader.setStyle(movementHeader.getStyle());
        gameHeader.setTextFill(sectionHeaderColor);
        
        Label menuHeader = new Label("MENU SHORTCUTS:");
        menuHeader.setStyle(movementHeader.getStyle());
        menuHeader.setTextFill(sectionHeaderColor);
        
        // Control items style - using bright blue for better visibility
        String controlItemStyle = 
            "-fx-font-family: 'Let\'s go Digital'; " +
            "-fx-font-size: 13px; " +
            "-fx-padding: 2 0;";
        
        // Bright cyan/blue color for control items
        Color brightBlue = Color.rgb(0, 255, 255); // #00FFFF
        
        // Movement controls
        Label leftControl = new Label("  Left Arrow / A  →  Move brick left");
        leftControl.setStyle(controlItemStyle);
        leftControl.setTextFill(brightBlue);
        Label rightControl = new Label("  Right Arrow / D →  Move brick right");
        rightControl.setStyle(controlItemStyle);
        rightControl.setTextFill(brightBlue);
        Label downControl = new Label("  Down Arrow / S  →  Move brick down (faster)");
        downControl.setStyle(controlItemStyle);
        downControl.setTextFill(brightBlue);
        Label hardDropControl = new Label("  Spacebar        →  Hard drop (instant drop)");
        hardDropControl.setStyle(controlItemStyle);
        hardDropControl.setTextFill(brightBlue);
        Label rotateControl = new Label("  Up Arrow / W    →  Rotate brick");
        rotateControl.setStyle(controlItemStyle);
        rotateControl.setTextFill(brightBlue);
        Label holdControl = new Label("  C                →  Hold/swap brick");
        holdControl.setStyle(controlItemStyle);
        holdControl.setTextFill(brightBlue);
        
        // Game controls
        Label pauseControl = new Label("  ESC              →  Pause/Resume game");
        pauseControl.setStyle(controlItemStyle);
        pauseControl.setTextFill(brightBlue);
        Label newGameControl = new Label("  N               →  New game");
        newGameControl.setStyle(controlItemStyle);
        newGameControl.setTextFill(brightBlue);
        
        // Menu shortcuts
        Label newGameShortcut = new Label("  Ctrl+N          →  New game");
        newGameShortcut.setStyle(controlItemStyle);
        newGameShortcut.setTextFill(brightBlue);
        Label exitShortcut = new Label("  Ctrl+Q          →  Exit game");
        exitShortcut.setStyle(controlItemStyle);
        exitShortcut.setTextFill(brightBlue);
        
        // Close button
        Button closeButton = new Button("Close");
        closeButton.setStyle(
            "-fx-font-family: 'Let\'s go Digital'; " +
            "-fx-font-size: 14px; " +
            "-fx-pref-width: 100px; " +
            "-fx-pref-height: 30px; " +
            "-fx-background-color: linear-gradient(#3a3a3a, #1f1f1f); " +
            "-fx-text-fill: yellow; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-border-color: #61a2b1; " +
            "-fx-border-width: 2px;"
        );
        // Check if game is paused before opening controls menu
        boolean wasPaused = isPause.getValue();
        
        closeButton.setOnAction(e -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
            // If game was paused when controls opened, resume it when controls close
            if (wasPaused) {
                resumeGame();
            }
        });
        
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
        
        Scene controlsScene = new Scene(vbox, 480, 450);
        controlsScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        
        // Allow ESC to close
        controlsScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                Stage stage = (Stage) controlsScene.getWindow();
                stage.close();
                // If game was paused when controls opened, resume it when controls close
                if (wasPaused) {
                    resumeGame();
                }
            }
        });
        
        Stage controlsStage = new Stage();
        controlsStage.initOwner(primaryStage);
        controlsStage.initModality(Modality.WINDOW_MODAL);
        controlsStage.initStyle(StageStyle.TRANSPARENT);
        controlsStage.setTitle("Game Controls");
        controlsStage.setScene(controlsScene);
        
        // Handle window close event (X button)
        controlsStage.setOnCloseRequest(e -> {
            // If game was paused when controls opened, resume it when controls close
            if (wasPaused) {
                resumeGame();
            }
        });
        
        // Center the controls window over the game window
        controlsStage.setX(primaryStage.getX() + (primaryStage.getWidth() - 480) / 2);
        controlsStage.setY(primaryStage.getY() + (primaryStage.getHeight() - 450) / 2);
        
        controlsStage.show();
        controlsStage.requestFocus();
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
        try {
            // Stop the game timeline if running
            if (timeLine != null) {
                timeLine.stop();
            }
            
            // Close pause menu if open
            closePauseMenu();
            
            // Get the current stage
            Stage stage = (Stage) gamePanel.getScene().getWindow();
            
            // Load the main menu
            URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent root = fxmlLoader.load();
            
            // Create and set the main menu scene
            Scene menuScene = new Scene(root, 600, 510);
            stage.setScene(menuScene);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to exit if menu loading fails
            Platform.exit();
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
        if (viewData == null || displayMatrix == null) {
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
}
