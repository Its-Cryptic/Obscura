package dev.cryptic.obscura.core;

import dev.cryptic.obscura.core.render.shader.Shader;
import dev.cryptic.obscura.logging.LogUtils;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GameRenderer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<ResourceLocation, Shader> shaders = new HashMap<>();
    private float fov = 90.0f;
    private float NEAR_PLANE = 0.1f;
    private float FAR_PLANE = 1000.0f;

    public static void render() {
//        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
//        glShaderSource(vertexShader, vertexShaderSource);
//        glCompileShader(vertexShader);
//
//        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
//        glShaderSource(fragmentShader, fragmentShaderSource);
//        glCompileShader(fragmentShader);
//
//        int shaderProgram = glCreateProgram();
//        glAttachShader(shaderProgram, vertexShader);
//        glAttachShader(shaderProgram, fragmentShader);
//        glLinkProgram(shaderProgram);
//
//        glDeleteShader(vertexShader);
//        glDeleteShader(fragmentShader);
//
//        float[] vertices = {
//                -0.5f, -0.5f, 0.0f,
//                0.5f, -0.5f, 0.0f,
//                0.0f, 0.5f, 0.0f
//        };
//
//        int vao = glGenVertexArrays();
//        int vbo = glGenBuffers();
//
//        glBindVertexArray(vao);
//        glBindBuffer(GL_ARRAY_BUFFER, vbo);
//        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
//
//        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
//        glEnableVertexAttribArray(0);
//
//        glUseProgram(shaderProgram);
//        glBindVertexArray(vao);
//        glDrawArrays(GL_TRIANGLES, 0, 3);
    }
}
