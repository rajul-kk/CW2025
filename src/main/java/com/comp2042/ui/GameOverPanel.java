package com.comp2042.ui;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;


public class GameOverPanel extends BorderPane {

    private Button newGameButton;

    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        
        newGameButton = new Button("New Game");
        newGameButton.getStyleClass().add("controlButton");
        newGameButton.setPrefWidth(150);
        
        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().addAll(gameOverLabel, newGameButton);
        
        setCenter(vbox);
    }

    public void setOnNewGameAction(javafx.event.EventHandler<ActionEvent> handler) {
        newGameButton.setOnAction(handler);
    }

}
