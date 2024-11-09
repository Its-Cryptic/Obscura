package dev.cryptic.obscura.core;

import dev.cryptic.obscura.core.render.shader.ShaderProgram;
import dev.cryptic.obscura.core.render.shader.ShaderType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30C.glDeleteVertexArrays;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    // The window handle
    private long handle;
    private int width, height;
    private String title;

    public Window(String title, int width, int height) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        handle = glfwCreateWindow(this.width, this.height, "Hello World!", NULL, NULL);
        if ( handle == NULL ) throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*
            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(handle, pWidth, pHeight);
            this.width = pWidth.get(0);
            this.height = pHeight.get(0);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(handle,
                    (vidmode.width() - this.width) / 2,
                    (vidmode.height() - this.height) / 2
            );
        }


        // Make the OpenGL context current
        glfwMakeContextCurrent(handle);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(handle);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//        ShaderProgram shaderProgram = new ShaderProgram();
//
//        Shader vertexShader = new Shader(ShaderType.VERTEX, ResourceLocation.shader("default.vsh"));
//        Shader fragmentShader = new Shader(ShaderType.FRAGMENT, ResourceLocation.shader("default.fsh"));
//        try {
//            vertexShader.compile();
//            fragmentShader.compile();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        ShaderProgram shaderProgram = ShaderProgram.Builder.of("default")
                .addShader(ShaderType.VERTEX, "default.vsh")
                .addShader(ShaderType.FRAGMENT, "default.fsh")
                .build();

        // Initialize VAO and VBO once here
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();

        int vao2 = glGenVertexArrays();
        int vbo2 = glGenBuffers();

        float[] vertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.0f,  0.5f, 0.0f
        };

        float[] vertices2 = {
                1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                0.0f,  -1.0f, -1.0f
        };

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0); // Unbind VBO
        glBindVertexArray(0); // Unbind VAO

        glBindVertexArray(vao2);
        glBindBuffer(GL_ARRAY_BUFFER, vbo2);
        glBufferData(GL_ARRAY_BUFFER, vertices2, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0); // Unbind VBO
        glBindVertexArray(0); // Unbind VAO

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        try {
            shaderProgram.createUniform("ModelMat");
            shaderProgram.createUniform("ViewMat");
            shaderProgram.createUniform("ProjMat");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(this.handle)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            render(shaderProgram, vao);
            //render(shaderProgram, vao2);
            update();
        }

        // Cleanup: Delete VAO and VBO after the loop ends
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteProgram(shaderProgram.getId());
    }

    private void update() {
        glfwSwapBuffers(this.handle);
        glfwPollEvents();
    }

    public static void render(ShaderProgram shaderProgram, int vao) {
        glUseProgram(shaderProgram.getId());
        shaderProgram.setUniform("ModelMat", createModelMatrix(position, rotation, scale));
        rotation.add(0.1f, 0.1f, 0.1f);
        shaderProgram.setUniform("ViewMat", GameRenderer.getMainCamera().getViewMatrix());
        shaderProgram.setUniform("ProjMat", GameRenderer.getMainCamera().getProjectionMatrix());
        //shaderProgram.setUniform("ProjMat", new Matrix4f().identity());
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 3);
        glBindVertexArray(0); // Unbind VAO
    }

    private static Vector3f position = new Vector3f(0, 0, -1);
    private static Vector3f rotation = new Vector3f(0, 0, 0);
    private static Vector3f scale = new Vector3f(1, 1, 1);

    public static Matrix4f createModelMatrix(Vector3f position, Vector3f rotation, Vector3f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(position);
        matrix.rotateX((float) Math.toRadians(rotation.x));
        matrix.rotateY((float) Math.toRadians(rotation.y));
        matrix.rotateZ((float) Math.toRadians(rotation.z));
        matrix.scale(scale);
        return matrix;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
