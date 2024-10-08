package dev.cryptic.obscura.test;

import dev.cryptic.obscura.Obscura;
import dev.cryptic.obscura.core.ILogic;
import dev.cryptic.obscura.core.entity.Entity;
import dev.cryptic.obscura.core.entity.Model;
import dev.cryptic.obscura.core.entity.ObjectLoader;
import dev.cryptic.obscura.core.RenderManager;
import dev.cryptic.obscura.core.Window;
import dev.cryptic.obscura.core.entity.Texture;
import dev.cryptic.obscura.core.utils.ResourceLocation;
import org.joml.Math;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class TestGame implements ILogic {
    private int direction = 0;
    private float color = 0.0f;

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final Window window;

    private Entity entity;

    public TestGame() {
        this.renderer = new RenderManager();
        this.window = Obscura.getWindow();
        this.loader = new ObjectLoader();
    }
    @Override
    public void init() throws Exception {
        renderer.init();

        float[] vertices = new float[] {
                -0.5f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f, 0.5f, 0.0f
        };

        int[] indices = new int[] {
                0, 1, 3,
                3, 1, 2
        };

        float[] textureCoords = new float[] {
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };

        Model model = loader.loadModel(vertices, textureCoords, indices);
        model.setTexture(new Texture(loader.loadTexture(ResourceLocation.texture("lodestar.png"))));
        entity = new Entity(model, new Vector3f(1, 0, 0), new Vector3f(0, 0, 0), 1.0f);
    }

    @Override
    public void input() {
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            Obscura.LOGGER.info("Up key pressed");
            direction = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            direction = -1;
        } else {
            direction = 0;
        }
    }

    @Override
    public void update() {
        color += direction * 0.01f;
        color = Math.clamp(0.0f, 1.0f, color);

        if (entity.getPosition().x < -1.5f) entity.getPosition().x = 1.5f;
        entity.getPosition().x -= 0.01f;
    }

    @Override
    public void render() {
        if (window.isResize()) {
            glViewport(0, 0, window.getWindowWidth(), window.getWindowHeight());
            window.setResize(false);
        }

        window.setClearColor(color, color, color, 1.0f);
        renderer.render(entity);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
