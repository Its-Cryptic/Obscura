package dev.cryptics.obscura.core.render;

import dev.cryptics.obscura.Obscura;
import dev.cryptics.obscura.core.Camera;
import dev.cryptics.obscura.core.MatrixStack;
import dev.cryptics.obscura.core.ResourceLocation;
import dev.cryptics.obscura.core.render.shader.Shader;
import dev.cryptics.obscura.core.render.shader.ShaderProgram;
import dev.cryptics.obscura.core.render.shader.ShaderType;
import dev.cryptics.obscura.model.IndexedModel;
import dev.cryptics.obscura.model.ObjModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;

public class GameRenderer extends ObscuraRenderer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceLocation, Shader> shaders = new HashMap<>();
    private Camera mainCamera = new Camera(90.0f, 0.1f, 1000.0f);
    private static Matrix4f projectionMatrix = new Matrix4f();

    private final IndexedModel suzanneModel = new ObjModel("suzanne");
    private ShaderProgram defaultShader;
    private ShaderProgram screenShader;

    private int fbo;
    private int textureColorBuffer;
    private int normalColorBuffer;
    private int depthBuffer;

    private int screenVAO;
    private int screenVBO;
    @Override
    public void init() {
        // Load shaders
        defaultShader = ShaderProgram.Builder.of("default")
                .addShader(ShaderType.VERTEX, "default")
                .addShader(ShaderType.FRAGMENT, "default")
                .build();

        screenShader = ShaderProgram.Builder.of("screen")
                .addShader(ShaderType.VERTEX, "screen")
                .addShader(ShaderType.FRAGMENT, "screen")
                .build();

        try {
            defaultShader.createUniform("ModelMat");
            defaultShader.createUniform("ViewMat");
            defaultShader.createUniform("ProjMat");

            screenShader.createUniform("screenTexture");
            screenShader.setUniform("screenTexture", 0);
        } catch (Exception e) {
            LOGGER.error("Failed to create uniform in default shader", e);
        }

        // Create framebuffer
        fbo = glGenFramebuffers();
        LOGGER.info("FBO: " + fbo);
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        textureColorBuffer = glGenTextures();
        normalColorBuffer = glGenTextures();
        depthBuffer = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthBuffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, Obscura.getWindow().getWidth(), Obscura.getWindow().getHeight(), 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthBuffer, 0);

        glBindTexture(GL_TEXTURE_2D, textureColorBuffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Obscura.getWindow().getWidth(), Obscura.getWindow().getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureColorBuffer, 0);

        glBindTexture(GL_TEXTURE_2D, normalColorBuffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Obscura.getWindow().getWidth(), Obscura.getWindow().getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, normalColorBuffer, 0);

        int[] drawBuffers = {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1};
        glDrawBuffers(drawBuffers);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            LOGGER.error("Framebuffer is not complete!");
        } else {
            LOGGER.info("Framebuffer is complete!");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        suzanneModel.loadModel();
        suzanneModel.init();

        float[] quadVertices = { // vertex attributes for a quad that fills the entire screen in Normalized Device Coordinates.
                // positions   // texCoords
                -1.0f,  1.0f,  0.0f, 1.0f,
                -1.0f, -1.0f,  0.0f, 0.0f,
                1.0f, -1.0f,  1.0f, 0.0f,

                -1.0f,  1.0f,  0.0f, 1.0f,
                1.0f, -1.0f,  1.0f, 0.0f,
                1.0f,  1.0f,  1.0f, 1.0f
        };

        screenVAO = glGenVertexArrays();
        screenVBO = glGenBuffers();
        glBindVertexArray(screenVAO);
        glBindBuffer(GL_ARRAY_BUFFER, screenVBO);
        glBufferData(GL_ARRAY_BUFFER, quadVertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glCullFace(GL_BACK);
    }

    private int degrees = 0;
    private float hash(float x) {
        return Math.sin(x);
    }

    @Override
    public void render(MatrixStack matrixStack) {
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        this.mainCamera.setPosition(new Vector3f(0, 4, 2));
        this.mainCamera.setPitch(-45);
        this.mainCamera.setYaw(0);
        //this.mainCamera.lookAt(new Vector3f(0, 0, 0));
        int instanceCount = 20;
        FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(instanceCount * 16);

        for (int i = 0; i < instanceCount; i++) {
            matrixStack.push();
            Vector3f position = new Vector3f((float) Math.random(), (float) Math.random(), 0);
            position.mul(2).sub(new Vector3f(1));
            position.mul(i);
            matrixStack.translate(position);
            matrixStack.getMatrix().get(16 * i, matrixBuffer);
            matrixStack.pop();
        }

        Obscura.getModelLoader().storeInstancedMatrixAttribute(suzanneModel, 3, matrixBuffer);


        matrixStack.push();

        matrixStack.rotateAround(new Quaternionf().rotateY(Math.toRadians(degrees)), 0, 0, -2);
        suzanneModel.render(defaultShader, matrixStack);

        matrixStack.push();
        matrixStack.translate(0, 0, -2);
        matrixStack.rotateAround(new Quaternionf().rotateY(Math.toRadians(degrees)), 0, 0, 2);
        suzanneModel.render(defaultShader, matrixStack);
        matrixStack.pop();

        matrixStack.pop();

        degrees += 1;

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDisable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(screenShader.getId());
        glBindVertexArray(screenVAO);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);
    }

    @Override
    public void cleanup() {
        // Delete shaders
        defaultShader.cleanup();
        screenShader.cleanup();

        // Delete framebuffer
        glDeleteFramebuffers(fbo);
        glDeleteTextures(textureColorBuffer);
        glDeleteTextures(normalColorBuffer);
        glDeleteTextures(depthBuffer);

        // Delete Screen VAO and VBO
        glDeleteVertexArrays(screenVAO);
        glDeleteBuffers(screenVBO);
    }

    public Camera getMainCamera() {
        return mainCamera;
    }
}
