package dev.cryptic.obscura.core;

import dev.cryptic.obscura.config.ObscuraContext;

import javax.annotation.Nonnull;

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
