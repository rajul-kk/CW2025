package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;

public class MenuController {
    
    @FXML
    private Button startButton;
    
    @FXML
    private Button exitButton;
    
    @FXML
    private void onStartButtonClick(ActionEvent event) {
        try {
            // Get the current stage
            Stage stage = (Stage) startButton.getScene().getWindow();
            
            // Load the game layout
            URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent root = fxmlLoader.load();
            GuiController guiController = fxmlLoader.getController();
            
            // Create and set the game scene
            Scene gameScene = new Scene(root, GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);
            stage.setScene(gameScene);
            
            // Initialize the game controller
            GameController gameController = new GameController(guiController);
            
            // Set up input handler for keyboard input
            // The InputHandler constructor automatically sets up key listeners
            new InputHandler(gameScene, gameController, guiController);
            
            // Set up layout manager for responsive resizing (if needed)
            // LayoutManager layoutManager = new LayoutManager(gameScene);
            // Example: layoutManager.bindWidth(somePane, 0.8);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onExitButtonClick(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}

