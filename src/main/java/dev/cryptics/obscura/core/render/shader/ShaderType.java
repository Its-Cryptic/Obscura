package dev.cryptics.obscura.core.render.shader;

import static org.lwjgl.opengl.GL43.*;

public enum ShaderType {
    VERTEX(GL_VERTEX_SHADER, "vsh"),
    FRAGMENT(GL_FRAGMENT_SHADER, "fsh"),
    COMPUTE(GL_COMPUTE_SHADER, "comp");

    private final int type;
    private final String fileExtension;
    ShaderType(int type, String fileExtension) {
        this.type = type;
        this.fileExtension = fileExtension;
    }

    public int create() {
        return glCreateShader(type);
    }

    public int getType() {
        return type;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
