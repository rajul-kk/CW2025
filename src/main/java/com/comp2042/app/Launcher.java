package com.comp2042.app;

import javafx.application.Application;

/**
 * Launcher class for the TetrisJFX application.
 * 
 * <p>This class provides the main entry point for the application. It launches
 * the JavaFX application by calling {@link Application#launch(Class, String...)}
 * with the {@link Main} class.
 * 
 * <p>This launcher is necessary because JavaFX applications require a separate
 * launcher class when using certain build tools and deployment scenarios.
 * 
 * <p>Usage:
 * <pre>{@code
 * java -cp ... com.comp2042.app.Launcher
 * }</pre>
 * 
 * @author Rajul Kabir
 * @version 1.0
 * @see Main
 */
public class Launcher {
    /**
     * Main entry point for the application.
     * 
     * <p>Launches the JavaFX application using the {@link Main} class.
     * This method is called by the JVM when the application is started.
     * 
     * @param args Command-line arguments (not currently used)
     */
    public static void main(String[] args) {
        // This version explicitly tells launch() to use "Main.class"
        Application.launch(Main.class, args); // <-- This is the fix
    }
}
