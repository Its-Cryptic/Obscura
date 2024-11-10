package dev.cryptic.obscura.config;

import dev.cryptic.obscura.Obscura;
import dev.cryptic.obscura.core.Window;

import java.util.function.Consumer;

public class ObscuraContext {
    private String[] args;
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
        window.run();
    }
}
