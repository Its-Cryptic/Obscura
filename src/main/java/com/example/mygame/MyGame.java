package com.example.mygame;

import dev.cryptic.obscura.config.ObscuraContext;
import dev.cryptic.obscura.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ObscuraGame(id = "my_game")
public class MyGame extends AbstractGame {
    private static final Logger LOGGER = LogManager.getLogger();
    public MyGame(ObscuraContext context) {
        String[] args = context.getArgs();
        context.createWindow("My Game", 1280, 720);
        LOGGER.info("Hello, Obscura!");
    }

    @Override
    public void start() {
    }
}
