package com.comp2042.app;

import com.comp2042.util.FontLoader;
import com.comp2042.model.GameConstants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main JavaFX Application class for TetrisJFX.
 * 
 * <p>This class serves as the entry point for the JavaFX application lifecycle.
 * It initializes the application by loading the digital font and displaying
 * the main menu scene.
 * 
 * <p>The application uses FXML for UI layout and follows the MVC architecture
 * pattern. The main menu is loaded from {@code mainMenu.fxml} and displayed
 * in a non-resizable window.
 * 
 * @author Rajul Kabir
 * @version 1.0
 * @see Launcher
 * @see com.comp2042.view.MenuController
 */
public class Main extends Application {

    /**
     * Initializes and starts the JavaFX application.
     * 
     * <p>This method is called by the JavaFX runtime when the application starts.
     * It performs the following operations:
     * <ol>
     *   <li>Loads the custom digital font using {@link FontLoader}</li>
     *   <li>Loads the main menu FXML layout</li>
     *   <li>Creates and displays the main menu scene</li>
     * </ol>
     * 
     * @param primaryStage The primary stage for the application
     * @throws Exception If there is an error loading the FXML file or initializing the application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the digital font before creating any UI elements
        FontLoader.loadFont();

        URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();

        primaryStage.setTitle(GameConstants.APP_TITLE);
        Scene scene = new Scene(root, GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }



}
