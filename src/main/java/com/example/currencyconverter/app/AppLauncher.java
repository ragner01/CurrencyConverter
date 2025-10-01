package com.example.currencyconverter.app;

import javafx.application.Application;

/**
 * Separate entry point to launch JavaFX without putting the main method on the Application subclass.
 * This avoids the "JavaFX runtime components are missing" error when running from IDE/plain java.
 */
public final class AppLauncher {
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
