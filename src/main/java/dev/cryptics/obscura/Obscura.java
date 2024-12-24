package dev.cryptics.obscura;

import dev.cryptics.obscura.config.ObscuraContext;
import dev.cryptics.obscura.core.AbstractGame;
import dev.cryptics.obscura.core.ModelLoader;
import dev.cryptics.obscura.core.Window;
import dev.cryptics.obscura.launcher.GameLauncher;
import org.apache.logging.log4j.*;

public class Obscura {
    public static final Logger LOGGER = LogManager. getLogger();
    private static Window window;
    private static ModelLoader modelLoader;
    private static ObscuraContext context;
    private static AbstractGame game;

    public static void main(String[] args) {
        LOGGER.info("Starting Obscura");
        context = new ObscuraContext(args);
        modelLoader = new ModelLoader();
        //game = GameLauncher.launchGame(context);
        //game.start();

        try {
            Thread.currentThread().setName("Main");
            AbstractGame game = GameLauncher.launchGame(context);
        } catch (Throwable throwable) {
            LOGGER.error("Failed to set thread name", throwable);
        }
    }

    public static void setWindow(Window window) {
        Obscura.window = window;
    }

    public static Window getWindow() {
        return window;
    }

    public static ObscuraContext getContext() {
        return context;
    }

    public static AbstractGame getGame() {
        return game;
    }

    public static ModelLoader getModelLoader() {
        return modelLoader;
    }
}