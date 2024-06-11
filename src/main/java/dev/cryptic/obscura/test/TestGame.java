package dev.cryptic.obscura.test;

import dev.cryptic.obscura.core.ILogic;
import dev.cryptic.obscura.core.RenderManager;
import dev.cryptic.obscura.core.Window;

import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class TestGame implements ILogic {
    private static final Logger LOGGER = Logger.getLogger(TestGame.class.getName());

    private int direction = 0;
    private float color = 0.0f;

    private final RenderManager renderer;
    private final Window window;

    public TestGame() {
        this.renderer = new RenderManager();
        this.window = new Window("Test Game", 1280, 720, true);
    }
    @Override
    public void init() throws Exception {
        renderer.init();
    }

    @Override
    public void input() {
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            LOGGER.info("Up key pressed");
            direction = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            direction = -1;
        } else {
            direction = 0;
        }
    }

    @Override
    public void update() {
        color += direction * 0.01f;
        if (color > 1) {
            color = 1;
        } else if (color <= 0) {
            color = 0;
        }
    }

    @Override
    public void render() {
        if (window.isResize()) {
            glViewport(0, 0, window.getWindowWidth(), window.getWindowHeight());
            window.setResize(false);
        }

        window.setClearColor(color, color, color, 1.0f);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }
}
