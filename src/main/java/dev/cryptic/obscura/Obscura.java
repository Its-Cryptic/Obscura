package dev.cryptic.obscura;

import dev.cryptic.obscura.core.EngineManager;
import dev.cryptic.obscura.core.Window;
import dev.cryptic.obscura.core.utils.WindowSizePresets;
import dev.cryptic.obscura.test.TestGame;

import org.apache.logging.log4j.*;

import static dev.cryptic.obscura.core.utils.Consts.*;

public class Obscura {
    public static final Logger LOGGER = LogManager.getLogger(Obscura.class.getSimpleName());
    private static Window window;
    private static TestGame game;

    public static void main(String[] args) {
        LOGGER.error("Starting Obscura");
        window = new Window(TITLE, WindowSizePresets.HD, true);
        game = new TestGame();
        EngineManager engine = new EngineManager();

        try {
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Window getWindow() {
        return window;
    }

    public static TestGame getGame() {
        return game;
    }
}