package dev.cryptic.obscura.core.entity;

import dev.cryptic.obscura.Obscura;
import dev.cryptic.obscura.core.entity.Model;
import dev.cryptic.obscura.core.utils.Utils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class ObjectLoader {
    // Vertex Array Objects
    private List<Integer> vaos = new ArrayList<>();
    // Vertex Buffer Objects
    private List<Integer> vbos = new ArrayList<>();

    public Model loadModel(float[] vertices, int[] indices) {
        Obscura.LOGGER.info("Loading model...");
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        unbind();
        return new Model(id, vertices.length / 3);
    }

    private int createVAO() {
        int id = glGenVertexArrays();
        vaos.add(id);
        glBindVertexArray(id);
        return id;
    }

    private void storeIndicesBuffer(int[] indices) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void unbind() {
        glBindVertexArray(0);
    }

    public void cleanup() {
        vaos.forEach(GL30::glDeleteVertexArrays);
        vbos.forEach(GL15::glDeleteBuffers);
    }
}
