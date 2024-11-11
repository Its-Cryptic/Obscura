package dev.cryptics.obscura.core.render;

import dev.cryptics.obscura.Obscura;
import dev.cryptics.obscura.core.Camera;
import dev.cryptics.obscura.core.MatrixStack;
import dev.cryptics.obscura.core.ObscuraRenderer;
import dev.cryptics.obscura.core.ResourceLocation;
import dev.cryptics.obscura.core.render.shader.Shader;
import dev.cryptics.obscura.core.render.shader.ShaderProgram;
import dev.cryptics.obscura.core.render.shader.ShaderType;
import dev.cryptics.obscura.model.IndexedModel;
import dev.cryptics.obscura.model.ObjModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;

public class GameRenderer extends ObscuraRenderer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceLocation, Shader> shaders = new HashMap<>();
    private Camera mainCamera = new Camera(90.0f, 0.1f, 1000.0f);
    private static Matrix4f projectionMatrix = new Matrix4f();

    private final IndexedModel suzanneModel = new ObjModel("suzanne");
    private ShaderProgram shaderProgram;
    private ShaderProgram screenShader;

    private int fbo;
    private int textureColorBuffer;
    private int normalColorBuffer;
    private int depthBuffer;

    private int screenVAO;
    private int screenVBO;
    @Override
    public void init() {
    }

    @Override
    public void render(MatrixStack matrixStack) {
    }

    @Override
    public void cleanup() {

    }

    public Camera getMainCamera() {
        return mainCamera;
    }
}
