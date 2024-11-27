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

        // 4. Unbind FBO and render to screen
        // --------------------------------------------------------------
        this.fbo.unbind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUseProgram(screenShader.getId());
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
