package dev.cryptics.obscura.core;

import dev.cryptics.obscura.model.IndexedModel;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class ModelLoader {
    private final List<Integer> vaos;
    private final List<Integer> vbos;

    public ModelLoader() {
        this.vaos = new ArrayList<>();
        this.vbos = new ArrayList<>();
    }

    public int loadToVAO(float[] positions, int[] indices) {
        int vaoID = createVAO();
        storeIndexBuffer(indices);
        storeInAttributeList(0, 3, positions);
        unbind();
        return vaoID;
    }

    public int loadToVAO(IndexedModel indexedModel) {
        int vaoID = createVAO();
        storeIndexBuffer(indexedModel.getIndices());
        storeInAttributeList(0, 3, indexedModel.getPositions());
        storeInAttributeList(1, 3, indexedModel.getNormals());
        unbind();
        return vaoID;
    }

    private int createVAO() {
        int id = glGenVertexArrays();
        vaos.add(id);
        glBindVertexArray(id);
        return id;
    }

    private void storeIndexBuffer(int[] indices) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    private void storeInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
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
