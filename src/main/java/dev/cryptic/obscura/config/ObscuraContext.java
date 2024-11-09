package dev.cryptic.obscura.config;

import dev.cryptic.obscura.Obscura;
import dev.cryptic.obscura.core.Window;

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
}
