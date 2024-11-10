package dev.cryptic.obscura.core.render.shader;

import dev.cryptic.obscura.core.ResourceLocation;

import static org.lwjgl.opengl.GL20.*;

public enum ShaderType {
    VERTEX(GL_VERTEX_SHADER, "vsh"),
    FRAGMENT(GL_FRAGMENT_SHADER, "fsh");
    private final int type;
    private final String fileExtension;
    ShaderType(int type, String fileExtension) {
        this.type = type;
        this.fileExtension = fileExtension;
    }

    public int getType() {
        return type;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
