package dev.cryptic.obscura.core.render.shader;

import dev.cryptic.obscura.core.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * A collection of shaders that are linked together to form a program.
 */
public class ShaderProgram implements IShader {
    private final int programId;
    private final Map<ShaderType, Shader> shaders;

    public ShaderProgram() {
        this.programId = glCreateProgram();
        this.shaders = new HashMap<>();
    }

    public void attachShader(Shader shader) {
        this.shaders.put(shader.getType(), shader);
        glAttachShader(this.programId, shader.getId());
    }

    @Override
    public int getId() {
        return this.programId;
    }

    public boolean isValidShaderProgram() {
        return this.programId != 0 && this.shaders.containsKey(ShaderType.VERTEX) && this.shaders.containsKey(ShaderType.FRAGMENT);
    }

    /**
     * Usage:
     * <pre>
     *     ShaderProgram shaderProgram = ShaderProgram.Builder.of("myShader")
     *          .addShader("default")
     *          .build();
     * </pre>
     */
    public static class Builder {
        private final String programName;
        private final Map<ShaderType, Shader> shaders;

        private Builder(String programName) {
            this.programName = programName;
            this.shaders = new HashMap<>();
        }

        public static Builder of(String programName) {
            return new Builder(programName);
        }

        public Builder addShader(ShaderType type, String shaderFile) {
            this.shaders.put(type, new Shader(type, ResourceLocation.shader(shaderFile)));
            return this;
        }

        public ShaderProgram build() {
            ShaderProgram shaderProgram = new ShaderProgram();
            this.shaders.values().forEach(shader -> {
                try {
                    shader.compile();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to compile shader: " + shader.getShaderLocation().getPath(), e);
                }
            });
            this.shaders.values().forEach(shaderProgram::attachShader);
            glLinkProgram(shaderProgram.getId());
            return shaderProgram;
        }

    }
}
