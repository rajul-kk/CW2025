package com.comp2042.app;

import javafx.application.Application; // <-- You may need this import

public class Launcher {
    public static void main(String[] args) {
        // This version explicitly tells launch() to use "Main.class"
        Application.launch(Main.class, args); // <-- This is the fix
    }
}
