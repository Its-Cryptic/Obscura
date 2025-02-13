package com.example.mygame;

import dev.cryptics.obscura.core.Camera;
import dev.cryptics.obscura.core.MatrixStack;
import dev.cryptics.obscura.core.render.ObscuraRenderer;
import dev.cryptics.obscura.core.render.shader.ShaderProgram;
import dev.cryptics.obscura.core.render.shader.ShaderType;
import dev.cryptics.obscura.core.util.TrianglePointDistribution;
import dev.cryptics.obscura.model.IndexedModel;
import dev.cryptics.obscura.model.ObjModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BACK;

public class TriangleDistributionRenderer extends ObscuraRenderer {
    private static final Logger LOGGER = LogManager.getLogger();
    private Camera camera = new Camera(90, 0.1f, 1000f);

    private final IndexedModel cube = new ObjModel("cube");
    private ShaderProgram pointShader;

    public int particleCount = 10000;
    public Vector3f[] particles;
    Vector3f[] vertices = new Vector3f[] {
            new Vector3f(-1, -1, 0),
            new Vector3f(1, -0.5f, 0),
            new Vector3f(0, 1, 0)
    };

    @Override
    public void init() {
        this.pointShader = ShaderProgram.Builder.of("particleRender")
                .addShader(ShaderType.VERTEX, "point/point")
                .addShader(ShaderType.FRAGMENT, "point/point")
                .build();

        try {
            pointShader.createUniform("ModelMat");
            pointShader.createUniform("ViewMat");
            pointShader.createUniform("ProjMat");
        } catch (Exception e) {
            LOGGER.error("Failed to create uniform in default shader", e);
        }

        this.cube.loadModel();
        this.cube.init();

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glCullFace(GL_BACK);

        //this.camera.setPosition(new Vector3f(0, 0, 5));

        this.particles = TrianglePointDistribution.distributePoints(vertices[0], vertices[1], vertices[2], particleCount);
    }

    @Override
    public void render(MatrixStack matrixStack) {
        this.camera.setPosition(new Vector3f(0, 0, 10));

        matrixStack.pushPop(() -> {
            matrixStack.translate(0, 0, 8.8);
            for (Vector3f vertex : vertices) {
                matrixStack.pushPop(() -> {
                    matrixStack.translate(vertex);
                    matrixStack.scale(0.01f);
                    this.cube.render(pointShader, matrixStack);
                });
            }

            for (Vector3f particle : particles) {
                matrixStack.push();
                matrixStack.translate(particle);
                matrixStack.scale(0.0025f);

                this.cube.render(pointShader, matrixStack);
                matrixStack.pop();
            }
//            matrixStack.pushPop(() -> {
//                Matrix4fc transform = TrianglePointDistribution.generateTransform(vertices[0], vertices[1], vertices[2]);
//                Vector3f point = new Vector3f(0.5f, 0.5f, 0.0f);
//                transform.transformPosition(point);
//                matrixStack.translate(point);
//                matrixStack.scale(0.005f);
//                this.cube.render(pointShader, matrixStack);
//            });
        });
    }

    @Override
    public void cleanup() {
        this.pointShader.cleanup();
    }

    @Override
    public Camera getMainCamera() {
        return camera;
    }
}
