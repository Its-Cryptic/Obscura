package dev.cryptics.obscura.core.render;

import dev.cryptics.obscura.core.Camera;
import dev.cryptics.obscura.core.MatrixStack;

public abstract class ObscuraRenderer {
    public abstract void init();
    public abstract void render(MatrixStack matrixStack);
    public abstract void cleanup();
    public abstract Camera getMainCamera();
}
