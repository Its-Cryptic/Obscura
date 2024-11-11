package dev.cryptics.obscura.core;

public abstract class ObscuraRenderer {
    public abstract void init();
    public abstract void render(MatrixStack matrixStack);
    public abstract void cleanup();
    public abstract Camera getMainCamera();
}
