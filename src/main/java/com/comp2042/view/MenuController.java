package com.comp2042.view;

import com.comp2042.util.FontLoader;
import com.comp2042.util.MusicManager;
import com.comp2042.model.GameConstants;
import com.comp2042.controller.GameController;
import com.comp2042.controller.InputHandler;
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
    
    @FXML
    private Button phantomButton;
    
    private final MusicManager musicManager = MusicManager.getInstance();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Play menu music when menu opens
        musicManager.playMenuMusic();
        
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
            if (phantomButton != null) {
                phantomButton.setFont(FontLoader.getFont(18));
            }
        }
    }
    
    @FXML
    private void onStartButtonClick(ActionEvent event) {
        try {
            // Stop menu music before starting game
            musicManager.stopMusic();
            
            // Get the current stage
            Stage stage = (Stage) startButton.getScene().getWindow();
            
            // Load the game layout
            URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent root = fxmlLoader.load();
            GuiController guiController = fxmlLoader.getController();
            
            // Disable phantom mode for classic mode
            guiController.setPhantomMode(false);
            
            // Create and set the game scene
            Scene gameScene = new Scene(root, GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);
            stage.setScene(gameScene);
            
            // Initialize the game controller
            GameController gameController = new GameController(guiController);
            
            // Set up input handler for keyboard input
            // The InputHandler constructor automatically sets up key listeners
            new InputHandler(gameScene, gameController, guiController);
            
            // Play classic mode music
            musicManager.playClassicMusic();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onPhantomButtonClick(ActionEvent event) {
        try {
            // Stop menu music before starting game
            musicManager.stopMusic();
            
            // Get the current stage
            Stage stage = (Stage) phantomButton.getScene().getWindow();
            
            // Load the game layout
            URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent root = fxmlLoader.load();
            GuiController guiController = fxmlLoader.getController();
            
            // Enable phantom mode
            guiController.setPhantomMode(true);
            
            // Create and set the game scene
            Scene gameScene = new Scene(root, GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);
            stage.setScene(gameScene);
            
            // Initialize the game controller
            GameController gameController = new GameController(guiController);
            
            // Set up input handler for keyboard input
            new InputHandler(gameScene, gameController, guiController);
            
            // Play phantom mode music
            musicManager.playPhantomMusic();
            
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

