package dev.cryptics.obscura.model;

//import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class VertexBuffer {
    private int vbo;
    private int vao;

    public VertexBuffer() {
    }

    public int createVertexArrayObject() {
        vao = glGenVertexArrays();
        return vao;
    }
}
