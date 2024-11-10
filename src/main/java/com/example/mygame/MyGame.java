package com.example.mygame;

import dev.cryptic.obscura.config.ObscuraContext;
import dev.cryptic.obscura.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ObscuraGame(id = "my_game")
public class MyGame extends AbstractGame {
    private static MyGame INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public MyGame(ObscuraContext context) {
        //super(context);
        INSTANCE = this;
        String[] args = context.getArgs();
        context.buildWindow(builder -> builder
                .setTitle("My Game :D")
                .setWindowSize(1280, 720)
                .addKeyCallback((window, key, scancode, action, mods) -> LOGGER.info("Pressed key: " + key))
                .addFramebufferSizeCallback((window, width, height) -> LOGGER.info("Resized window to: " + width + "x" + height))
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
