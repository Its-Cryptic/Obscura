package dev.cryptic.obscura.core;

import dev.cryptic.obscura.Obscura;
import dev.cryptic.obscura.core.entity.Model;
import dev.cryptic.obscura.core.shaders.ShaderManager;
import dev.cryptic.obscura.core.utils.ResourceLocation;

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
        shader.createVertexShader(ResourceLocation.shader("my_shader.vsh").open());
        shader.createFragmentShader(ResourceLocation.shader("my_shader.fsh").open());
        shader.link();
    }

    public void render(Model model) {
        clear();
        shader.bind();
        glBindVertexArray(model.getId());
        glEnableVertexAttribArray(0);
        glDrawArrays(GL_TRIANGLES, 0, model.getVertexCount());
        glDisableVertexAttribArray(0);
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
