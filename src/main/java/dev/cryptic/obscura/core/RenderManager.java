package dev.cryptic.obscura.core;

import dev.cryptic.obscura.Obscura;
import dev.cryptic.obscura.core.entity.Entity;
import dev.cryptic.obscura.core.shaders.ShaderManager;
import dev.cryptic.obscura.core.utils.ResourceLocation;
import dev.cryptic.obscura.core.utils.Transformation;
import org.joml.Vector2i;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class RenderManager {
    private final Window window;
    private ShaderManager shader;

    public RenderManager() {
        this.window = Obscura.getWindow();
    }

    public void init() throws Exception {
        shader = new ShaderManager();
        shader.createVertexShader(ResourceLocation.shader("my_shader.vsh").openAsString());
        shader.createGeometryShader(ResourceLocation.shader("my_shader.gsh").openAsString());
        shader.createFragmentShader(ResourceLocation.shader("my_shader.fsh").openAsString());
        shader.link();

        shader.createUniform("TextureSampler");
        shader.createUniform("ModelMatrix");
        shader.createUniform("ViewMatrix");
        shader.createUniform("ProjectionMatrix");

    }

    public void render(Entity entity, Camera camera) {
        clear();
        shader.bind();
        shader.setUniform("TextureSampler", 0);
        shader.setUniform("ModelMatrix", Transformation.createModelMatrix(entity));
        shader.setUniform("ViewMatrix", Transformation.getViewMatrix(camera));
        shader.setUniform("ProjectionMatrix", window.updateProjectionMatrix());
        glBindVertexArray(entity.getModel().getId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glActiveTexture(GL_TEXTURE31);
        glBindTexture(GL_TEXTURE_2D, entity.getModel().getTexture().getId());
        glDrawElements(GL_TRIANGLES, entity.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.unbind();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        shader.cleanup();
    }
}
