package com.comp2042;

import javafx.animation.KeyFrame;
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
import javafx.scene.control.TextArea;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
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
    private GridPane brickPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private Label scoreLabel;

    private Rectangle[][] displayMatrix;
    
    private Stage pauseMenuStage;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

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
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
                if (keyEvent.getCode() == KeyCode.P || keyEvent.getCode() == KeyCode.SPACE) {
                    togglePause(null);
                    keyEvent.consume();
                }
            }
        });
        gameOverPanel.setVisible(false);

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

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);


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
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }


    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
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
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
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
        gameOverPanel.setVisible(true);
        closePauseMenu();
        isGameOver.setValue(Boolean.TRUE);
        isPause.setValue(Boolean.FALSE); // Ensure pause is cleared on game over
    }

    @FXML
    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
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
        
        // Allow P or ESC to resume the game from the pause menu
        pauseScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.P || e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ESCAPE) {
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Controls");
        alert.setHeaderText("Tetris Controls");
        
        String controlsText = 
            "MOVEMENT:\n" +
            "  Left Arrow / A  -  Move brick left\n" +
            "  Right Arrow / D -  Move brick right\n" +
            "  Down Arrow / S  -  Move brick down (faster)\n" +
            "  Up Arrow / W    -  Rotate brick\n\n" +
            "GAME CONTROLS:\n" +
            "  P / Space       -  Pause/Resume game\n" +
            "  N               -  New game\n\n" +
            "MENU SHORTCUTS:\n" +
            "  Ctrl+N          -  New game\n" +
            "  Ctrl+Q          -  Exit game";
        
        TextArea textArea = new TextArea(controlsText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(12);
        textArea.setPrefColumnCount(40);
        textArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");
        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().add(textArea);
        
        alert.getDialogPane().setContent(vbox);
        alert.getDialogPane().setPrefWidth(400);
        alert.setResizable(true);
        alert.showAndWait();
        gamePanel.requestFocus();
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
}
