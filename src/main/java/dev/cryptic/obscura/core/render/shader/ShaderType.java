package dev.cryptic.obscura.core.render.shader;

import static org.lwjgl.opengl.GL20.*;

public enum ShaderType {
    VERTEX(GL_VERTEX_SHADER),
    FRAGMENT(GL_FRAGMENT_SHADER);
    private final int type;
    ShaderType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
