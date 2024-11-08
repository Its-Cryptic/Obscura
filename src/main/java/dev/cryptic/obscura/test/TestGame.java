package dev.cryptic.obscura.test;

import dev.cryptic.obscura.Obscura;
import dev.cryptic.obscura.core.Camera;
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
    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final Window window;

    private Entity entity;
    private Camera camera;

    private Vector3f cameraInc;

    public TestGame() {
        this.renderer = new RenderManager();
        this.window = Obscura.getWindow();
        this.loader = new ObjectLoader();
        this.camera = new Camera();
        this.cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
    }
    @Override
    public void init() throws Exception {
        renderer.init();

        float[] vertices = new float[] {
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
        };
        float[] texCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.0f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        float[] normals = new float[] {
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
        };
        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,
                8, 10, 11, 9, 8, 11,
                12, 13, 7, 5, 12, 7,
                14, 15, 6, 4, 14, 6,
                16, 18, 19, 17, 16, 19,
                4, 6, 7, 5, 4, 7,
        };

        Model model = loader.loadModel(vertices, texCoords, indices);
        model.setTexture(new Texture(loader.loadTexture(ResourceLocation.texture("lodestar.png"))));
        entity = new Entity(model, new Vector3f(0, 0, -2), new Vector3f(0, 0, 0), 1.0f);
    }

    @Override
    public void input() {
        cameraInc.set(0.0f, 0.0f, 0.0f);
        if (window.isKeyPressed(GLFW_KEY_W)) cameraInc.y = 1.0f;
        if (window.isKeyPressed(GLFW_KEY_S)) cameraInc.y = -1.0f;

        if (window.isKeyPressed(GLFW_KEY_A)) cameraInc.x = -1.0f;
        if (window.isKeyPressed(GLFW_KEY_D)) cameraInc.x = 1.0f;

        if (window.isKeyPressed(GLFW_KEY_Z)) cameraInc.z = -1.0f;
        if (window.isKeyPressed(GLFW_KEY_X)) cameraInc.z = 1.0f;

    }

    @Override
    public void update() {
        float CAMERA_MOVEMENT_SPEED = 0.01f;
        camera.move(cameraInc.x * CAMERA_MOVEMENT_SPEED, cameraInc.y * CAMERA_MOVEMENT_SPEED, cameraInc.z * CAMERA_MOVEMENT_SPEED);

        entity.incRot(0.0f, 0.5f, 0.0f);
    }

    @Override
    public void render() {
        if (window.isResize()) {
            glViewport(0, 0, window.getWindowWidth(), window.getWindowHeight());
            window.setResize(false);
        }

        window.setClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        renderer.render(entity, camera);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
