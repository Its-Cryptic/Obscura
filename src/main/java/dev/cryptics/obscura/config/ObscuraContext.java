package dev.cryptics.obscura.config;

import dev.cryptics.obscura.Obscura;
import dev.cryptics.obscura.core.render.GameRenderer;
import dev.cryptics.obscura.core.render.ObscuraRenderer;
import dev.cryptics.obscura.core.Window;

import java.util.function.Consumer;

public class ObscuraContext {
    private String[] args;
    private ObscuraRenderer renderer = new GameRenderer();
    private Thread rendererThread;
    public ObscuraContext(String[] args) {
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }

    public Window createWindow(String title, int width, int height) {
        Window window = new Window(title, width, height);
        Obscura.setWindow(window);
        window.run();
        return window;
    }

    public void buildWindow(Consumer<Window.Builder> windowBuilder) {
        Window.Builder builder = new Window.Builder();
        windowBuilder.accept(builder);
        Window window = builder.build();
        Obscura.setWindow(window);
        rendererThread = new Thread(window);
        rendererThread.start();
    }

    public void setRenderer(ObscuraRenderer renderer) {
        this.renderer = renderer;
    }

    public ObscuraRenderer getRenderer() {
        return renderer;
    }
}
