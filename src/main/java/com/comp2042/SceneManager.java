package com.comp2042;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Manages scene navigation and stage transitions for the application.
 * Handles loading FXML files and switching between scenes.
 */
public class SceneManager {
    
    /**
     * Loads an FXML file and returns the root Parent node.
     * 
     * @param fxmlFileName The name of the FXML file (e.g., "mainMenu.fxml")
     * @param loaderClass The class to use for resource loading (typically the controller class)
     * @return The loaded Parent node, or null if loading fails
     */
    public static Parent loadFXML(String fxmlFileName, Class<?> loaderClass) {
        try {
            URL location = loaderClass.getClassLoader().getResource(fxmlFileName);
            if (location == null) {
                System.err.println("FXML file not found: " + fxmlFileName);
                return null;
            }
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            return fxmlLoader.load();
        } catch (Exception e) {
            System.err.println("Error loading FXML file: " + fxmlFileName);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets the Stage from a given Node by traversing up the scene graph.
     * 
     * @param node Any node in the scene
     * @return The Stage containing the node, or null if not found
     */
    public static Stage getStage(Node node) {
        if (node == null) {
            return null;
        }
        
        Scene scene = node.getScene();
        if (scene == null) {
            return null;
        }
        
        return (Stage) scene.getWindow();
    }
    
    /**
     * Switches the scene on the given stage to the specified FXML file.
     * 
     * @param stage The stage to switch scenes on
     * @param fxmlFileName The name of the FXML file to load
     * @param loaderClass The class to use for resource loading
     * @param width The width of the new scene
     * @param height The height of the new scene
     * @return true if the scene was successfully switched, false otherwise
     */
    public static boolean switchToScene(Stage stage, String fxmlFileName, Class<?> loaderClass, 
                                        int width, int height) {
        if (stage == null) {
            return false;
        }
        
        Parent root = loadFXML(fxmlFileName, loaderClass);
        if (root == null) {
            return false;
        }
        
        Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        return true;
    }
    
    /**
     * Switches the scene on the stage containing the given node to the specified FXML file.
     * Convenience method that gets the stage from the node automatically.
     * 
     * @param node Any node in the current scene
     * @param fxmlFileName The name of the FXML file to load
     * @param loaderClass The class to use for resource loading
     * @param width The width of the new scene
     * @param height The height of the new scene
     * @return true if the scene was successfully switched, false otherwise
     */
    public static boolean switchToSceneFromNode(Node node, String fxmlFileName, Class<?> loaderClass,
                                                int width, int height) {
        Stage stage = getStage(node);
        if (stage == null) {
            return false;
        }
        return switchToScene(stage, fxmlFileName, loaderClass, width, height);
    }
    
    /**
     * Switches to the main menu scene from the given node.
     * 
     * @param node Any node in the current scene
     * @param loaderClass The class to use for resource loading
     * @return true if the scene was successfully switched, false otherwise
     */
    public static boolean switchToMainMenu(Node node, Class<?> loaderClass) {
        return switchToSceneFromNode(node, "mainMenu.fxml", loaderClass, 
                                    GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);
    }
    
    /**
     * Handles scene transition errors by exiting the application.
     * Should be called when a critical scene transition fails.
     */
    public static void handleTransitionError() {
        System.err.println("Critical error during scene transition. Exiting application.");
        Platform.exit();
    }
}

