package dev.cryptics.obscura.core.render;

import dev.cryptics.obscura.Obscura;
import dev.cryptics.obscura.core.Camera;
import dev.cryptics.obscura.core.MatrixStack;
import dev.cryptics.obscura.core.ResourceLocation;
import dev.cryptics.obscura.core.render.framebuffer.FBO;
import dev.cryptics.obscura.core.render.shader.Shader;
import dev.cryptics.obscura.core.render.shader.ShaderProgram;
import dev.cryptics.obscura.core.render.shader.ShaderType;
import dev.cryptics.obscura.model.IndexedModel;
import dev.cryptics.obscura.model.ObjModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL43.*;

public class GameRenderer extends ObscuraRenderer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceLocation, Shader> shaders = new HashMap<>();
    private Camera mainCamera = new Camera(90.0f, 0.1f, 1000.0f);
    private final IndexedModel suzanneModel = new ObjModel("suzanne");
    private ShaderProgram defaultShader;
    private ShaderProgram screenShader;
    private ShaderProgram computeShader;
    private int computeTexture;
    private int computeWidth = 16;
    private int computeHeight = 16;
    private int ssbo;

    private int[] maxWorkGroupSize = new int[3];
    private int[] maxWorkGroupCount = new int[3];
    private int maxWorkGroupInvocations;

//    private int fbo;
//    private int textureColorBuffer;
//    private int normalColorBuffer;
//    private int depthBuffer;

    private FBO fbo;

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

        computeShader = ShaderProgram.Builder.of("compute_ssbo")
                .addShader(ShaderType.COMPUTE, "compute/compute_ssbo")
                .build();

        //unbind
        glUseProgram(0);
//        computeTexture = glGenTextures();
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, computeTexture);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, this.computeWidth, this.computeHeight, 0, GL_RGBA, GL_FLOAT, 0);
//
//        glBindImageTexture(0, computeTexture, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
//
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, computeTexture);
//        LOGGER.info("Compute texture: " + computeTexture);

        float[] data = new float[100];
        FloatBuffer buffer = BufferUtils.createFloatBuffer(100);
        buffer.put(data);
        buffer.flip();

        ssbo = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, buffer, GL_DYNAMIC_COPY);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);

        for (int i = 0; i < 3; i++) {
            glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_SIZE, i, maxWorkGroupSize);
            glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, i, maxWorkGroupCount);
            //LOGGER.info("Max work group size: " + maxWorkGroupSize[i]);
            //LOGGER.info("Max work group count: " + maxWorkGroupCount[i]);
        }
        maxWorkGroupInvocations = glGetInteger(GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS);
        //LOGGER.info("Max work group invocations: " + maxWorkGroupInvocations);

        try {
            defaultShader.createUniform("ModelMat");
            defaultShader.createUniform("ViewMat");
            defaultShader.createUniform("ProjMat");

            glUseProgram(screenShader.getId());
            screenShader.createUniform("gAlbedoSpec");
            screenShader.setUniform("gAlbedoSpec", 0);
            screenShader.createUniform("gNormal");
            screenShader.setUniform("gNormal", 1);
            screenShader.createUniform("Resolution");
        } catch (Exception e) {
            LOGGER.error("Failed to create uniform in default shader", e);
        }

        this.fbo = new FBO(true);
        this.fbo.init();

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
        // 1. Bind FBO for Geometry Pass
        // --------------------------------------------------------------
        this.fbo.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        this.mainCamera.setPosition(new Vector3f(8, 12, 0));
        this.mainCamera.setPitch(-45);
        this.mainCamera.setYaw(-45);

        // 2. Generate 1000 model matrices for instanced rendering
        // --------------------------------------------------------------
        int instanceCount = 1000;
        FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(instanceCount * 16);

        float scale = Obscura.getWindow().getImguiLayer().sliderValue2[0];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10; k++) {
                    matrixStack.push();
                    matrixStack.translate(i * scale, j * scale, k * scale);
                    matrixStack.getMatrix().get(16 * (i + j * 10 + k * 100), matrixBuffer);
                    matrixStack.pop();
                }
            }
        }

        Obscura.getModelLoader().storeInstancedMatrixAttribute(suzanneModel, 3, matrixBuffer);

        // 3. Render the model with instanced rendering
        // --------------------------------------------------------------
        matrixStack.push();
        matrixStack.translate(-5, 0, -14);
        //matrixStack.scale(0.2f, 0.2f, 0.2f);
        matrixStack.rotate(new Quaternionf().rotateY(Math.toRadians(degrees)));
        suzanneModel.render(defaultShader, matrixStack);
        matrixStack.pop();

        degrees += 1;

        // 3.5. Compute Shader
        // --------------------------------------------------------------
        computeShader.bind();
        glDispatchCompute(this.computeWidth, this.computeHeight, 1);
        // make sure writing to image has finished before read
        //glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT); // GL_SHADER_STORAGE_BARRIER_BIT
        glMemoryBarrier(GL_BUFFER_UPDATE_BARRIER_BIT);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);

        //LOGGER.info("SSBO: " + ssbo);
        FloatBuffer ssboData = glMapBuffer(GL_SHADER_STORAGE_BUFFER, GL_READ_ONLY).asFloatBuffer();
        for (int i = 0; i < 100; i++) {
            LOGGER.info("SSBO[" + i + "]: " + ssboData.get(i));
        }
        glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);



        // 4. Unbind FBO and render to screen
        // --------------------------------------------------------------
        this.fbo.unbind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        screenShader.bind();
        screenShader.setUniform("Resolution", new Vector2f(Obscura.getWindow().getWidth(), Obscura.getWindow().getHeight()));
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, this.fbo.getAlbedoID());
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, this.fbo.getNormalID());

        glBindVertexArray(screenVAO);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);

        glBindFramebuffer(GL_READ_FRAMEBUFFER, this.fbo.getFbo()); // read from our framebuffer
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0); // write to default framebuffer
        int width = Obscura.getWindow().getWidth();
        int height = Obscura.getWindow().getHeight();
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        this.fbo.unbind();
    }

    @Override
    public void cleanup() {
        // Delete shaders
        defaultShader.cleanup();
        screenShader.cleanup();
        computeShader.cleanup();
        //glDeleteTextures(computeTexture);

        // Delete framebuffer
        this.fbo.cleanup();

        // Delete Screen VAO and VBO
        glDeleteVertexArrays(screenVAO);
        glDeleteBuffers(screenVBO);
    }

    public Camera getMainCamera() {
        return mainCamera;
    }
}
