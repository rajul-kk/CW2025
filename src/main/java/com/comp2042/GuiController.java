package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

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

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private java.util.List<javafx.scene.Node> currentFallingBlockNodes = new java.util.ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.C) {
                        ViewData viewData = eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER));
                        if (viewData != null) {
                            refreshBrick(viewData);
                        }
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    togglePause(null);
                    keyEvent.consume();
                }
            }
        });
        if (gameOverScreen != null) {
            gameOverScreen.setVisible(false);
            gameOverScreen.setManaged(false);
        }

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
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

        // Initialize falling block rectangles directly on gamePanel
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        updateFallingBlock(brick);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                // AQUA - Cyan gem
                returnPaint = new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0.0, Color.rgb(0, 255, 255)),
                    new Stop(0.3, Color.rgb(0, 200, 255)),
                    new Stop(0.7, Color.rgb(0, 150, 200)),
                    new Stop(1.0, Color.rgb(0, 100, 150)));
                break;
            case 2:
                // BLUEVIOLET - Purple gem
                returnPaint = new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0.0, Color.rgb(138, 43, 226)),
                    new Stop(0.3, Color.rgb(120, 30, 200)),
                    new Stop(0.7, Color.rgb(100, 20, 170)),
                    new Stop(1.0, Color.rgb(80, 10, 140)));
                break;
            case 3:
                // DARKGREEN - Green gem
                returnPaint = new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0.0, Color.rgb(0, 200, 0)),
                    new Stop(0.3, Color.rgb(0, 150, 0)),
                    new Stop(0.7, Color.rgb(0, 100, 0)),
                    new Stop(1.0, Color.rgb(0, 60, 0)));
                break;
            case 4:
                // YELLOW - Yellow gem
                returnPaint = new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0.0, Color.rgb(255, 255, 0)),
                    new Stop(0.3, Color.rgb(255, 220, 0)),
                    new Stop(0.7, Color.rgb(220, 180, 0)),
                    new Stop(1.0, Color.rgb(180, 140, 0)));
                break;
            case 5:
                // RED - Red gem
                returnPaint = new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0.0, Color.rgb(255, 0, 0)),
                    new Stop(0.3, Color.rgb(220, 0, 0)),
                    new Stop(0.7, Color.rgb(180, 0, 0)),
                    new Stop(1.0, Color.rgb(140, 0, 0)));
                break;
            case 6:
                // BEIGE - Orange gem
                returnPaint = new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0.0, Color.rgb(255, 200, 150)),
                    new Stop(0.3, Color.rgb(255, 180, 120)),
                    new Stop(0.7, Color.rgb(220, 150, 100)),
                    new Stop(1.0, Color.rgb(180, 120, 80)));
                break;
            case 7:
                // BURLYWOOD - Brown gem
                returnPaint = new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0.0, Color.rgb(222, 184, 135)),
                    new Stop(0.3, Color.rgb(200, 160, 110)),
                    new Stop(0.7, Color.rgb(170, 130, 90)),
                    new Stop(1.0, Color.rgb(140, 100, 70)));
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }
    
    private Color getBorderColor(int i) {
        switch (i) {
            case 1: // AQUA
                return Color.rgb(150, 255, 255);
            case 2: // BLUEVIOLET
                return Color.rgb(180, 100, 255);
            case 3: // DARKGREEN
                return Color.rgb(100, 255, 100);
            case 4: // YELLOW
                return Color.rgb(255, 255, 150);
            case 5: // RED
                return Color.rgb(255, 150, 150);
            case 6: // BEIGE
                return Color.rgb(255, 220, 180);
            case 7: // BURLYWOOD
                return Color.rgb(240, 200, 160);
            default:
                return Color.WHITE;
        }
    }


    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            // Remove old rectangles and add new ones at updated positions
            updateFallingBlock(brick);
        }
    }

    private void updateFallingBlock(ViewData brick) {
        // Remove old falling block rectangles from gamePanel
        for (javafx.scene.Node node : currentFallingBlockNodes) {
            gamePanel.getChildren().remove(node);
        }
        currentFallingBlockNodes.clear();
        
        // Clear the rectangles array
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        
        int xPos = brick.getxPosition();
        int yPos = brick.getyPosition();
        int[][] brickData = brick.getBrickData();
        
        // Create and add new rectangles at correct grid positions
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    rectangle.setFill(getFillColor(brickData[i][j]));
                    rectangle.setStroke(getBorderColor(brickData[i][j]));
                    rectangle.setStrokeType(StrokeType.INSIDE);
                    rectangle.setStrokeWidth(1.5);
                    rectangle.setArcHeight(9);
                    rectangle.setArcWidth(9);
                    
                    rectangles[i][j] = rectangle;
                    
                    // Calculate grid position: column = xPos + j, row = yPos + i - 2 (offset for hidden rows)
                    int gridColumn = xPos + j;
                    int gridRow = yPos + i - 2; // -2 because board starts at row 2
                    
                    // Only add if within visible bounds
                    if (gridRow >= 0 && gridColumn >= 0 && gridColumn < 10) {
                        gamePanel.add(rectangle, gridColumn, gridRow);
                        currentFallingBlockNodes.add(rectangle);
                    }
                } else {
                    rectangles[i][j] = null;
                }
            }
        }
    }

    public void refreshGameBackground(int[][] board) {
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
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                
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
                
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        if (scoreLabel != null && integerProperty != null) {
            scoreLabel.textProperty().bind(integerProperty.asString());
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
        Platform.exit();
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
        togglePause(actionEvent);
    }

    public void togglePause(ActionEvent actionEvent) {
        if (isGameOver.getValue()) {
            return; // Don't allow pausing when game is over
        }
        
        boolean currentlyPaused = isPause.getValue();
        
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
        resumeButton.setStyle("-fx-font-size: 16px; -fx-pref-width: 150px; -fx-pref-height: 35px;");
        resumeButton.setDefaultButton(true);
        resumeButton.setOnAction(e -> resumeGame());
        
        Button newGameButton = new Button("New Game");
        newGameButton.setStyle("-fx-font-size: 16px; -fx-pref-width: 150px; -fx-pref-height: 35px;");
        newGameButton.setOnAction(e -> {
            closePauseMenu();
            newGame(null);
        });
        
        Button controlsButton = new Button("Controls");
        controlsButton.setStyle("-fx-font-size: 16px; -fx-pref-width: 150px; -fx-pref-height: 35px;");
        controlsButton.setOnAction(e -> {
            closePauseMenu();
            showControls(null);
        });
        
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-size: 16px; -fx-pref-width: 150px; -fx-pref-height: 35px;");
        exitButton.setOnAction(e -> exitGame(null));
        
        pauseMenu.getChildren().addAll(pauseTitle, resumeButton, newGameButton, controlsButton, exitButton);
        
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
            leftControl, rightControl, downControl, rotateControl, holdControl,
            gameHeader,
            pauseControl, newGameControl,
            menuHeader,
            newGameShortcut, exitShortcut,
            new Label(""), // Spacer
            closeButton
        );
        
        Scene controlsScene = new Scene(vbox, 480, 420);
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
        controlsStage.setY(primaryStage.getY() + (primaryStage.getHeight() - 420) / 2);
        
        controlsStage.show();
        controlsStage.requestFocus();
    }

    @FXML
    public void showAbout(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About TetrisJFX");
        alert.setHeaderText("TetrisJFX");
        alert.setContentText(
            "A classic Tetris game implementation in JavaFX.\n\n" +
            "Clear rows by completing horizontal lines.\n" +
            "Score points by clearing multiple rows at once!\n\n" +
            "Version 1.0"
        );
        alert.showAndWait();
        gamePanel.requestFocus();
    }

    @FXML
    public void exitGame(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void drawNextBlock1(Block block) {
        if (nextBlockPane1 == null || block == null) {
            return;
        }
        nextBlockPane1.getChildren().clear();
        int[][] shape = block.getShape();
        int blockSize = 15; // Smaller size for preview
        int offsetX = (int) (nextBlockPane1.getPrefWidth() - shape[0].length * blockSize) / 2;
        int offsetY = (int) (nextBlockPane1.getPrefHeight() - shape.length * blockSize) / 2;
        
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    Rectangle rectangle = new Rectangle(blockSize, blockSize);
                    rectangle.setFill(getFillColor(shape[i][j]));
                    rectangle.setStroke(getBorderColor(shape[i][j]));
                    rectangle.setStrokeType(StrokeType.INSIDE);
                    rectangle.setStrokeWidth(1.0);
                    rectangle.setArcHeight(5);
                    rectangle.setArcWidth(5);
                    rectangle.setLayoutX(offsetX + j * blockSize);
                    rectangle.setLayoutY(offsetY + i * blockSize);
                    nextBlockPane1.getChildren().add(rectangle);
                }
            }
        }
    }

    public void drawNextBlock2(Block block) {
        if (nextBlockPane2 == null || block == null) {
            return;
        }
        nextBlockPane2.getChildren().clear();
        int[][] shape = block.getShape();
        int blockSize = 15; // Smaller size for preview
        int offsetX = (int) (nextBlockPane2.getPrefWidth() - shape[0].length * blockSize) / 2;
        int offsetY = (int) (nextBlockPane2.getPrefHeight() - shape.length * blockSize) / 2;
        
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    Rectangle rectangle = new Rectangle(blockSize, blockSize);
                    rectangle.setFill(getFillColor(shape[i][j]));
                    rectangle.setStroke(getBorderColor(shape[i][j]));
                    rectangle.setStrokeType(StrokeType.INSIDE);
                    rectangle.setStrokeWidth(1.0);
                    rectangle.setArcHeight(5);
                    rectangle.setArcWidth(5);
                    rectangle.setLayoutX(offsetX + j * blockSize);
                    rectangle.setLayoutY(offsetY + i * blockSize);
                    nextBlockPane2.getChildren().add(rectangle);
                }
            }
        }
    }

    public void drawNextBlock3(Block block) {
        if (nextBlockPane3 == null || block == null) {
            return;
        }
        nextBlockPane3.getChildren().clear();
        int[][] shape = block.getShape();
        int blockSize = 15; // Smaller size for preview
        int offsetX = (int) (nextBlockPane3.getPrefWidth() - shape[0].length * blockSize) / 2;
        int offsetY = (int) (nextBlockPane3.getPrefHeight() - shape.length * blockSize) / 2;
        
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    Rectangle rectangle = new Rectangle(blockSize, blockSize);
                    rectangle.setFill(getFillColor(shape[i][j]));
                    rectangle.setStroke(getBorderColor(shape[i][j]));
                    rectangle.setStrokeType(StrokeType.INSIDE);
                    rectangle.setStrokeWidth(1.0);
                    rectangle.setArcHeight(5);
                    rectangle.setArcWidth(5);
                    rectangle.setLayoutX(offsetX + j * blockSize);
                    rectangle.setLayoutY(offsetY + i * blockSize);
                    nextBlockPane3.getChildren().add(rectangle);
                }
            }
        }
    }

    public void drawHoldBlock(Block block) {
        if (holdBlockPane == null) {
            return;
        }
        holdBlockPane.getChildren().clear();
        if (block == null) {
            return;
        }
        int[][] shape = block.getShape();
        int blockSize = 15; // Size for preview in hold box
        int offsetX = (int) (holdBlockPane.getPrefWidth() - shape[0].length * blockSize) / 2;
        int offsetY = (int) (holdBlockPane.getPrefHeight() - shape.length * blockSize) / 2;
        
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    Rectangle rectangle = new Rectangle(blockSize, blockSize);
                    rectangle.setFill(getFillColor(shape[i][j]));
                    rectangle.setStroke(getBorderColor(shape[i][j]));
                    rectangle.setStrokeType(StrokeType.INSIDE);
                    rectangle.setStrokeWidth(1.0);
                    rectangle.setArcHeight(5);
                    rectangle.setArcWidth(5);
                    rectangle.setLayoutX(offsetX + j * blockSize);
                    rectangle.setLayoutY(offsetY + i * blockSize);
                    holdBlockPane.getChildren().add(rectangle);
                }
            }
        }
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

        // Animate each rectangle that is part of the locked block
        // Note: brickData is indexed as [column][row] based on merge function
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[j][i] != 0) {
                    // Calculate the board position (matching merge function: targetX = x + i, targetY = y + j)
                    int boardCol = xPos + i;  // i maps to column (X)
                    int boardRow = yPos + j;  // j maps to row (Y)

                    // Check bounds and get the corresponding rectangle
                    if (boardRow >= 2 && boardRow < displayMatrix.length && 
                        boardCol >= 0 && boardCol < displayMatrix[boardRow].length) {
                        Rectangle rectangle = displayMatrix[boardRow][boardCol];
                        
                        if (rectangle != null) {
                            // Create a scale transition that pulses the rectangle
                            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), rectangle);
                            scaleTransition.setFromX(1.0);
                            scaleTransition.setFromY(1.0);
                            scaleTransition.setToX(1.2);
                            scaleTransition.setToY(1.2);
                            scaleTransition.setAutoReverse(true);
                            scaleTransition.setCycleCount(2);
                            scaleTransition.play();
                        }
                    }
                }
            }
        }
    }
}
