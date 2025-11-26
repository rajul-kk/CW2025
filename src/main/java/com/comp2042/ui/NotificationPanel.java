package com.comp2042.ui;

import com.comp2042.model.GameConstants;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class NotificationPanel extends BorderPane {

    public NotificationPanel(String text) {
        setMinHeight(200);
        setMinWidth(220);
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        final Effect glow = new Glow(0.6);
        score.setEffect(glow);
        score.setTextFill(Color.WHITE);
        setCenter(score);

    }
    
    /**
     * Centers this notification panel over the specified game board.
     * Calculates the center position relative to the game board and positions
     * the notification in the notification group's local coordinate space.
     * 
     * @param gameBoard The game board BorderPane to center over
     * @param notificationGroup The Parent (Group/Pane) where the notification will be added
     */
    public void centerOverGameBoard(javafx.scene.layout.BorderPane gameBoard, Parent notificationGroup) {
        if (gameBoard == null || notificationGroup == null || notificationGroup.getScene() == null) {
            return;
        }
        
        // Get the gameboard's bounds in the scene
        Bounds gameBoardBounds = gameBoard.localToScene(gameBoard.getBoundsInLocal());
        
        // Calculate center position in scene coordinates
        double centerXScene = gameBoardBounds.getMinX() + gameBoardBounds.getWidth() / 2 - this.getMinWidth() / 2;
        double centerYScene = gameBoardBounds.getMinY() + gameBoardBounds.getHeight() / 2 - this.getMinHeight() / 2;
        
        // Convert scene coordinates to notificationGroup's local coordinates
        Point2D localPoint = notificationGroup.sceneToLocal(centerXScene, centerYScene);
        this.setLayoutX(localPoint.getX());
        this.setLayoutY(localPoint.getY());
    }

    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(2000), this);
        TranslateTransition tt = new TranslateTransition(Duration.millis(GameConstants.NOTIFICATION_DURATION_MS), this);
        tt.setToY(this.getLayoutY() - 40);
        ft.setFromValue(1);
        ft.setToValue(0);
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.remove(NotificationPanel.this);
            }
        });
        transition.play();
    }
}
