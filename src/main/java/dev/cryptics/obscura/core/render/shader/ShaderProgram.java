package dev.cryptics.obscura.core.render.shader;

import dev.cryptics.obscura.core.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL20.*;

/**
 * A collection of shaders that are linked together to form a program.
 */
public class ShaderProgram implements IShader {
    private final int programId;
    private final Map<ShaderType, Shader> shaders;
    private final Map<String, Integer> uniforms;

    public ShaderProgram() {
        this.programId = glCreateProgram();
        this.shaders = new HashMap<>();
        this.uniforms = new HashMap<>();
    }

    public void attachShader(Shader shader) {
        this.shaders.put(shader.getType(), shader);
        glAttachShader(this.programId, shader.getId());
    }

    public void bind() {
        glUseProgram(this.programId);
    }

    @Override
    public int getId() {
        return this.programId;
    }

    public boolean isValidShaderProgram() {
        return this.programId != 0 && this.shaders.containsKey(ShaderType.VERTEX) && this.shaders.containsKey(ShaderType.FRAGMENT);
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) throw new Exception("Could not find uniform: " + uniformName);
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, float value) {
        ifUniformPresent(uniformName, uniformLocation -> {
            glUniform1f(uniformLocation, value);
        });
    }

    public void setUniform(String uniformName, Vector2f vector2f) {
        ifUniformPresent(uniformName, uniformLocation -> {
            glUniform2f(uniformLocation, vector2f.x, vector2f.y);
        });
    }

    public void setUniform(String uniformName, Vector3f vector3f) {
        ifUniformPresent(uniformName, uniformLocation -> {
            glUniform3f(uniformLocation, vector3f.x, vector3f.y, vector3f.z);
        });
    }

    public void setUniform(String uniformName, Vector4f vector4f) {
        ifUniformPresent(uniformName, uniformLocation -> {
            glUniform4f(uniformLocation, vector4f.x, vector4f.y, vector4f.z, vector4f.w);
        });
    }

    public void setUniform(String uniformName, Matrix4f matrix4f) {
        ifUniformPresent(uniformName, uniformLocation -> {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                glUniformMatrix4fv(uniformLocation, false, matrix4f.get(stack.mallocFloat(16)));
            }
        });
    }

    public void setUniform(String uniformName, int value) {
        ifUniformPresent(uniformName, uniformLocation -> {
            glUniform1i(uniformLocation, value);
        });
    }

    public void cleanup() {
        glDeleteProgram(this.programId);
    }

    private void ifUniformPresent(String uniformName, Consumer<Integer> consumer) {
        Integer uniformLocation = uniforms.get(uniformName);
        if (uniformLocation != null) {
            consumer.accept(uniformLocation);
        }
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
            this.shaders.put(type, new Shader(type, ResourceLocation.shader(shaderFile, type)));
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
