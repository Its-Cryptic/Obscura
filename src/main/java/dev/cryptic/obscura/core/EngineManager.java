package dev.cryptic.obscura.core;

import dev.cryptic.obscura.Obscura;
import org.lwjgl.glfw.GLFWErrorCallback;

import static dev.cryptic.obscura.core.utils.Consts.*;
import static org.lwjgl.glfw.GLFW.*;

public class EngineManager {
    public static final long NANOSECOND = 1000000000L;
    public static final float FRAMERATE = 1000.0F;

    private static int fps;
    private static float frameTime = 1.0f / FRAMERATE;
    private boolean isRunning;

    private Window window;
    private MouseInput mouseInput;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;

    private void init() throws Exception {
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = Obscura.getWindow();
        gameLogic = Obscura.getGame();
        mouseInput = new MouseInput();

        window.init();
        mouseInput.init();
        gameLogic.init();
    }

    public void start() throws Exception {
        init();
        if (isRunning) return;
        run();
    }

    public void run() {
        this.isRunning = true;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while (isRunning) {
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) NANOSECOND;
            frameCounter += passedTime;

            input();

            while (unprocessedTime > frameTime) {
                render = true;
                unprocessedTime -= frameTime;

                if (window.windowShouldClose()) stop();

                if (frameCounter >= NANOSECOND) {
                    setFps(frames);
                    window.setTitle(TITLE + " | " + getFps());
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if (render) {
                update();
                render();
                frames++;
            }
        }
        cleanup();
    }

    public void stop() {
        if (!isRunning) return;
        isRunning = false;
    }

    private void input() {
        mouseInput.input();
        gameLogic.input();
    }

    private void render() {
        gameLogic.render();
        window.update();
    }

    private void update() {
        gameLogic.update();
    }

    private void cleanup() {
        window.cleanup();
        gameLogic.cleanup();
        errorCallback.free();
        glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }
}
