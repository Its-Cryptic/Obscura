package dev.cryptic.obscura.core.render.shader;

import dev.cryptic.obscura.core.ResourceLocation;

import static org.lwjgl.opengl.GL20.*;

public class Shader implements IShader {
    private int shaderId;
    private final ShaderType type;
    private final ResourceLocation shaderLocation;
    private String source;

    public Shader(ShaderType type, ResourceLocation shaderLocation) {
        this.type = type;
        this.shaderLocation = shaderLocation;
        this.source = shaderLocation.open();
    }

    public void compile() throws Exception {
        this.shaderId = glCreateShader(this.type.getType());
        glShaderSource(this.shaderId, this.source);
        glCompileShader(this.shaderId);
        if (glGetShaderi(this.shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new Exception("Failed to compile shader: " + glGetShaderInfoLog(this.shaderId, 1024));
        }
    }

    public int getId() {
        return this.shaderId;
    }

    public ShaderType getType() {
        return this.type;
    }

    public ResourceLocation getShaderLocation() {
        return this.shaderLocation;
    }
}
