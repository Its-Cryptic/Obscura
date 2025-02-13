package com.example.mygame;

import dev.cryptics.obscura.Obscura;
import dev.cryptics.obscura.config.ObscuraContext;
import dev.cryptics.obscura.core.*;
import dev.cryptics.obscura.launcher.ObscuraGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@ObscuraGame(id = "my_game")
public class MyGame extends AbstractGame {
    private static MyGame INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public MyGame(ObscuraContext context) {
        INSTANCE = this;
        String[] args = context.getArgs();
        context.buildWindow(windowBuilder -> windowBuilder
                .setTitle("My Game :D")
                .setWindowSize(1280, 720)
                //.setWindowSize(1920, 1080)
                //.setWindowSize(2560, 1400)
                .addKeyCallback((window, key, scancode, action, mods) -> {
                    LOGGER.info("Pressed key: " + key);
                    if (key == GLFW.GLFW_KEY_M && action == GLFW.GLFW_PRESS) {
                        Obscura.getWindow().setWindowName("My Game :D & M");
                    }
                })
                .addFramebufferSizeCallback((window, width, height) -> {
                    LOGGER.info("Resized window to: " + width + "x" + height);
                })
        );
        context.setRenderer(new TriangleDistributionRenderer());
        LOGGER.info("Hello, Obscura!");
    }

    @Override
    public void start() {
    }

    public static MyGame getInstance() {
        return INSTANCE;
    }
}
