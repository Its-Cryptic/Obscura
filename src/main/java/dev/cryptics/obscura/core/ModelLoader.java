package dev.cryptics.obscura.core;

import dev.cryptics.obscura.model.IndexedModel;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class ModelLoader {
    private final List<Integer> vaos;
    private final List<Integer> vbos;
    private final Map<IndexedModel, Integer> modelVaos;

    public ModelLoader() {
        this.vaos = new ArrayList<>();
        this.vbos = new ArrayList<>();
        this.modelVaos = new java.util.HashMap<>();
    }

    public int loadToVAO(IndexedModel indexedModel) {
        int vaoID = createVAO(indexedModel);
        storeIndexBuffer(indexedModel.getIndices());
        storeInAttributeList(0, 3, indexedModel.getPositions(), 3);
        storeInAttributeList(1, 3, indexedModel.getNormals(), 3);
        storeInAttributeList(2, 2, indexedModel.getTextureCoordinates(), 2);
        unbind();
        return vaoID;
    }

    private int createVAO(IndexedModel indexedModel) {
        int id = glGenVertexArrays();
        vaos.add(id);
        modelVaos.put(indexedModel, id);
        glBindVertexArray(id);
        return id;
    }

    private void storeIndexBuffer(int[] indices) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    private void storeInAttributeList(int attributeNumber, int coordinateSize, float[] data, int stride) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, stride * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
    }

    private boolean bindModel(IndexedModel indexedModel) {
        if (modelVaos.containsKey(indexedModel)) {
            if (modelVaos.get(indexedModel) != 0) {
                glBindVertexArray(modelVaos.get(indexedModel));
                return true;
            }
        }
        return false;
    }

    public void storeInstancedMatrixAttribute(IndexedModel indexedModel, int startAttributeIndex, FloatBuffer buffer) {
        if(!bindModel(indexedModel)) return; // Bind VAO in order to store instanced data in it
        int matrixVBO = glGenBuffers();
        vbos.add(matrixVBO);
        glBindBuffer(GL_ARRAY_BUFFER, matrixVBO);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW); // Use dynamic draw as the data will be updated every frame
        for (int i = 0; i < 4; i++) {
            glVertexAttribPointer(startAttributeIndex + i, 4, GL_FLOAT, false, 16 * Float.BYTES, i * 4 * Float.BYTES);
            glEnableVertexAttribArray(startAttributeIndex + i);
            glVertexAttribDivisor(startAttributeIndex + i, 1); // This is what makes it instanced
        }
    }
    private void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0); // Unbind VBO
        glBindVertexArray(0); // Unbind VAO
    }
    public void cleanup() {
        vaos.forEach(GL30::glDeleteVertexArrays);
        vbos.forEach(GL30::glDeleteBuffers);
    }
}
