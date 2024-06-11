package dev.cryptic.obscura;

import dev.cryptic.obscura.core.EngineManager;
import dev.cryptic.obscura.core.Window;
import dev.cryptic.obscura.core.utils.WindowSizePresets;
import dev.cryptic.obscura.test.TestGame;
import org.lwjgl.opengl.GL11;

import static dev.cryptic.obscura.core.utils.Consts.*;

public class Launcher {
    private static Window window;
    private static TestGame game;

    public static void main(String[] args) {
        window = new Window(TITLE, WindowSizePresets.HD, true);
        window.init();

        while (!window.windowShouldClose()) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            window.update();
        }
        window.cleanup();
    }

    public static Window getWindow() {
        return window;
    }

    public static TestGame getGame() {
        return game;
    }
}