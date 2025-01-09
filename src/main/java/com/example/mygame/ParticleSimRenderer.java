package com.example.mygame;

import dev.cryptics.obscura.core.Camera;
import dev.cryptics.obscura.core.MatrixStack;
import dev.cryptics.obscura.core.render.ObscuraRenderer;
import dev.cryptics.obscura.core.render.shader.ShaderProgram;
import dev.cryptics.obscura.core.render.shader.ShaderType;
import dev.cryptics.obscura.model.IndexedModel;
import dev.cryptics.obscura.model.ObjModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.GL_BUFFER_UPDATE_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

public class ParticleSimRenderer extends ObscuraRenderer {
    private static final Logger LOGGER = LogManager.getLogger();
    private Camera camera = new Camera(90, 0.1f, 1000f);

    private final IndexedModel suzanneModel = new ObjModel("cube");

    private ShaderProgram particleUpdateShader;
    private ShaderProgram particleRenderShader;

    private int ssbo;
    public int particleCount = 10000;
    private int floatPerParticle = 4 + 4;


    private static float randomBetween(float min, float max) {
        return (float) Math.random() * (max - min) + min;
    }

    @Override
    public void init() {
        this.particleUpdateShader = ShaderProgram.Builder.of("particleUpdate")
                .addShader(ShaderType.COMPUTE, "particle/particle_update")
                .build();

        this.particleRenderShader = ShaderProgram.Builder.of("particleRender")
                .addShader(ShaderType.VERTEX, "particle/particle")
                .addShader(ShaderType.FRAGMENT, "particle/particle")
                .build();

        glUseProgram(0);

        float[] data = new float[this.particleCount * this.floatPerParticle];
        for (int i = 0; i < this.particleCount; i++) {
            // Position
            data[i * this.floatPerParticle] = randomBetween(-1, 1) * 5;
            data[i * this.floatPerParticle + 1] = randomBetween(-1, 1) * 5;
            data[i * this.floatPerParticle + 2] = randomBetween(-1, 1) * 5;
            data[i * this.floatPerParticle + 3] = (float) 0.0; // Padding

            // Velocity
            data[i * this.floatPerParticle + 4] = randomBetween(-1, 1) * 0;
            data[i * this.floatPerParticle + 5] = randomBetween(-1, 1) * 0;
            data[i * this.floatPerParticle + 6] = randomBetween(-1, 1) * 0;
            data[i * this.floatPerParticle + 7] = (float) 0.0; // Padding
        }
        FloatBuffer buffer = BufferUtils.createFloatBuffer(this.particleCount * this.floatPerParticle);
        buffer.put(data);
        buffer.flip();

        ssbo = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, buffer, GL_DYNAMIC_COPY);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);

        try {
            particleRenderShader.createUniform("ModelMat");
            particleRenderShader.createUniform("ViewMat");
            particleRenderShader.createUniform("ProjMat");
        } catch (Exception e) {
            LOGGER.error("Failed to create uniform in default shader", e);
        }

        this.suzanneModel.loadModel();
        this.suzanneModel.init();

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glCullFace(GL_BACK);

        this.camera.setPosition(new Vector3f(8, 12, 0));
        this.camera.setPitch(-45);
        this.camera.setYaw(-45);
    }

    @Override
    public void render(MatrixStack matrixStack) {
        // 1.0. Compute Shader
        // --------------------------------------------------------------
        particleUpdateShader.bind();
        int size = (particleCount + 128 -1) / 128;
        glDispatchCompute(size, 1, 1);
        // make sure writing to image has finished before read
        //glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT); // GL_SHADER_STORAGE_BARRIER_BIT
        glMemoryBarrier(GL_BUFFER_UPDATE_BARRIER_BIT);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);

//        FloatBuffer ssboData = glMapBuffer(GL_SHADER_STORAGE_BUFFER, GL_READ_ONLY).asFloatBuffer();
//        for (int i = 0; i < this.particleCount; i++) {
//            //LOGGER.info("SSBO[" + i + "]: " + ssboData.get(i));
//            LOGGER.info("Particle[" + i + "]: " + ssboData.get(i * this.floatPerParticle) + ", " + ssboData.get(i * this.floatPerParticle + 1) + ", " + ssboData.get(i * this.floatPerParticle + 2));
//        }
//        glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);

        matrixStack.push();
        matrixStack.translate(-5, 0, -14);
        matrixStack.scale(0.1f, 0.1f, 0.1f);
        this.suzanneModel.render(particleRenderShader, matrixStack);
        matrixStack.pop();
    }

    @Override
    public void cleanup() {
        LOGGER.info("Cleaning up ParticleSimRenderer");
        //particleShader.cleanup();
        particleUpdateShader.cleanup();
        //emitterShader.cleanup();

        glDeleteBuffers(ssbo);
    }

    @Override
    public Camera getMainCamera() {
        return camera;
    }
}
