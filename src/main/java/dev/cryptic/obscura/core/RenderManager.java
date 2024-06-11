package dev.cryptic.obscura.core;

import dev.cryptic.obscura.Launcher;

import static org.lwjgl.opengl.GL11.*;

public class RenderManager {
    private final Window window;

    public RenderManager() {
        this.window = Launcher.getWindow();
    }

    public void init() throws Exception {

    }

    public void render() {

    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {

    }
}
