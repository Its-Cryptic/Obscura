package com.example.mygame;

import dev.cryptics.obscura.config.ObscuraContext;
import dev.cryptics.obscura.core.*;
import dev.cryptics.obscura.core.render.GameRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
                .addKeyCallback((window, key, scancode, action, mods) -> {
                    LOGGER.info("Pressed key: " + key);
                })
                .addFramebufferSizeCallback((window, width, height) -> {
                    LOGGER.info("Resized window to: " + width + "x" + height);
                })
        );
        LOGGER.info("Hello, Obscura!");
    }

    @Override
    public void start() {
    }

    public static MyGame getInstance() {
        return INSTANCE;
    }
}
