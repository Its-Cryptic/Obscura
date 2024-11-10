package dev.cryptics.obscura.core;

import dev.cryptics.obscura.config.ObscuraContext;

public abstract class AbstractGame {
    private ObscuraContext context;

//    public AbstractGame(@Nonnull ObscuraContext context) {
//        this.context = context;
//    }
    public abstract void start();

    public ObscuraContext getContext() {
        return context;
    }
}
