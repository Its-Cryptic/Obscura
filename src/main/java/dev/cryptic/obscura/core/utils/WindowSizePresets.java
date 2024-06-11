package dev.cryptic.obscura.core.utils;

public enum WindowSizePresets {

    HD("HD", 1280, 720),
    HD_PLUS("HD+", 1600, 900),
    FHD("FHD", 1920, 1080),
    QHD("QHD", 2560, 1440),
    UHD("UHD", 3840, 2160);

    private final String name;
    private final int width;
    private final int height;

    WindowSizePresets(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
