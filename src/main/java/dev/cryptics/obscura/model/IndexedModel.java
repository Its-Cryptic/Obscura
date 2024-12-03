package dev.cryptics.obscura.model;

import dev.cryptics.obscura.Obscura;
import dev.cryptics.obscura.core.MatrixStack;
import dev.cryptics.obscura.core.render.GameRenderer;
import dev.cryptics.obscura.core.ResourceLocation;
import dev.cryptics.obscura.core.render.shader.ShaderProgram;
import dev.cryptics.obscura.model.data.IndexedMesh;
import dev.cryptics.obscura.model.data.Vertex;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

public abstract class IndexedModel {
    protected List<Vertex> vertices;
    protected List<IndexedMesh> meshes;
    protected List<Integer> bakedIndices;
    //protected List<ModelModifier<?>> modifiers;
    protected String modelId;

    private int vao;

    public IndexedModel(String modelId) {
        this.modelId = modelId;
        this.vertices = new ArrayList<>();
        this.meshes = new ArrayList<>();
        this.bakedIndices = new ArrayList<>();
    }

    public abstract void loadModel();

//    public void applyModifiers() {
//        if (modifiers != null) {
//            modifiers.forEach(modifier -> modifier.apply(this));
//        }
//    }

    public void init() {
        this.bakeIndices();

        this.vao = Obscura.getModelLoader().loadToVAO(this);
    }

    public void render(ShaderProgram shaderProgram, MatrixStack matrixStack) {
        shaderProgram.bind();

        shaderProgram.setUniform("ModelMat", matrixStack.getMatrix());
        shaderProgram.setUniform("ViewMat", Obscura.getContext().getRenderer().getMainCamera().getViewMatrix());
        shaderProgram.setUniform("ProjMat", Obscura.getContext().getRenderer().getMainCamera().getProjectionMatrix());

        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        //glDrawElements(GL_TRIANGLES, this.bakedIndices.size(), GL_UNSIGNED_INT, 0);
        glDrawElementsInstanced(GL_TRIANGLES, this.bakedIndices.size(), GL_UNSIGNED_INT, 0, 1000);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glBindVertexArray(0);
    }

    public void bakeIndices() {
        for (IndexedMesh mesh : this.meshes) {
            this.bakedIndices.addAll(mesh.getIndices());
        }
    }

    public List<Vertex> getVertices() {
        return this.vertices;
    }

    public List<IndexedMesh> getMeshes() {
        return this.meshes;
    }

    public void setMeshes(List<IndexedMesh> meshes) {
        this.meshes = meshes;
    }

    public List<Integer> getBakedIndices() {
        return this.bakedIndices;
    }

    public int getVao() {
        return this.vao;
    }

    public String getModelId() {
        return this.modelId;
    }

    public ResourceLocation getAssetLocation() {
        return ResourceLocation.model(this.modelId + ".obj");
    }

    public float[] getPositions() {
        float[] positions = new float[this.vertices.size() * 3];
        this.vertices.forEach(vertex -> {
            int index = this.vertices.indexOf(vertex);
            positions[index * 3] = vertex.getPosition().x;
            positions[index * 3 + 1] = vertex.getPosition().y;
            positions[index * 3 + 2] = vertex.getPosition().z;
        });
        return positions;
    }

    public float[] getNormals() {
        float[] normals = new float[this.vertices.size() * 3];
        this.vertices.forEach(vertex -> {
            int index = this.vertices.indexOf(vertex);
            normals[index * 3] = vertex.getNormal().x;
            normals[index * 3 + 1] = vertex.getNormal().y;
            normals[index * 3 + 2] = vertex.getNormal().z;
        });
        return normals;
    }


    public float[] getTextureCoordinates() {
        float[] textureCoordinates = new float[this.vertices.size() * 2];
        this.vertices.forEach(vertex -> {
            int index = this.vertices.indexOf(vertex);
            textureCoordinates[index * 2] = vertex.getUv().x;
            textureCoordinates[index * 2 + 1] = vertex.getUv().y;
        });
        return textureCoordinates;
    }

    public int[] getIndices() {
        return this.bakedIndices.stream().mapToInt(i -> i).toArray();
    }

    public void cleanup() {
        glDeleteVertexArrays(this.vao);
        glDeleteBuffers(this.vao);
    }
}
