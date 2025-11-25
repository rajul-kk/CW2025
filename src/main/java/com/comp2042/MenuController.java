package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private Button startButton;
    
    @FXML
    private Button exitButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Apply digital font to title and buttons using FontLoader
        String fontFamily = FontLoader.getFontFamily();
        if (fontFamily != null && !fontFamily.equals("System")) {
            // Apply font to title (64px as per CSS)
            if (titleLabel != null) {
                titleLabel.setFont(FontLoader.getFont(64));
            }
            
            // Apply font to buttons (18px as per CSS)
            if (startButton != null) {
                startButton.setFont(FontLoader.getFont(18));
            }
            if (exitButton != null) {
                exitButton.setFont(FontLoader.getFont(18));
            }
        }
    }
    
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

