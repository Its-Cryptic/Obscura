package dev.cryptics.obscura.core;

import dev.cryptics.obscura.Obscura;
import dev.cryptics.obscura.core.consts.*;
import dev.cryptics.obscura.core.render.framebuffer.FrameBuffer;
import dev.cryptics.obscura.core.render.shader.ShaderProgram;
import dev.cryptics.obscura.core.render.shader.ShaderType;
import dev.cryptics.obscura.imgui.ImGuiLayer;
import dev.cryptics.obscura.model.ObjModel;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    private static Logger LOGGER = LogManager.getLogger();
    private long handle;
    private int width, height;
    private String title;

    private GLFWKeyCallbackI keyCallback;
    private GLFWFramebufferSizeCallbackI framebufferSizeCallback;

    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private ImGuiLayer imguiLayer;

    public Window(String title, int width, int height) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.imguiLayer = new ImGuiLayer();
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        initWindow();
        initImgui();

        initCamera();

        loop();

        destoyImGui();
        destroyWindow();
    }

    private void destoyImGui() {
        imGuiGlfw.dispose();
        imGuiGl3.dispose();
        ImGui.destroyContext();
    }

    private void destroyWindow() {
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void initImgui() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        imGuiGlfw.init(handle, true);
        imGuiGl3.init("#version 330");
    }

    private void initWindow() {
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
        handle = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if ( handle == NULL ) throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(handle, this::onKeyCallback);
        glfwSetFramebufferSizeCallback(handle, this::onFramebufferSizeCallback);

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

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
    }

    public void initCamera() {
        Camera camera = Obscura.getContext().getRenderer().getMainCamera();
        camera.updateViewMatrix();
        camera.updateProjectionMatrix();
    }

    private void loop() {

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        ShaderProgram defaultShader = ShaderProgram.Builder.of("default")
                .addShader(ShaderType.VERTEX, "default")
                .addShader(ShaderType.FRAGMENT, "default")
                .build();

        ShaderProgram screenShader = ShaderProgram.Builder.of("screen")
                .addShader(ShaderType.VERTEX, "screen")
                .addShader(ShaderType.FRAGMENT, "screen")
                .build();

        // FBO Config
        int fbo = glGenFramebuffers();
        LOGGER.info("FBO: " + fbo);
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        int textureColorBuffer = glGenTextures();
        int normalColorBuffer = glGenTextures();
        int depthBuffer = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthBuffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, this.width, this.height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthBuffer, 0);

        glBindTexture(GL_TEXTURE_2D, textureColorBuffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureColorBuffer, 0);

        glBindTexture(GL_TEXTURE_2D, normalColorBuffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, normalColorBuffer, 0);

        int[] drawBuffers = {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1};
        glDrawBuffers(drawBuffers);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            LOGGER.error("Framebuffer is not complete!");
        } else {
            LOGGER.info("Framebuffer is complete!");
        }

        // Initialize VAO and VBO once here
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();

        int screenVAO = glGenVertexArrays();
        int screenVBO = glGenBuffers();

        ObjModel model = new ObjModel("suzanne");
        model.loadModel();
        model.init();

        float[] vertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.0f,  0.5f, 0.0f
        };

        float[] quadVertices = { // vertex attributes for a quad that fills the entire screen in Normalized Device Coordinates.
                // positions   // texCoords
                -1.0f,  1.0f,  0.0f, 1.0f,
                -1.0f, -1.0f,  0.0f, 0.0f,
                1.0f, -1.0f,  1.0f, 0.0f,

                -1.0f,  1.0f,  0.0f, 1.0f,
                1.0f, -1.0f,  1.0f, 0.0f,
                1.0f,  1.0f,  1.0f, 1.0f
        };

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0); // Unbind VBO
        glBindVertexArray(0); // Unbind VAO

        glBindVertexArray(screenVAO);
        glBindBuffer(GL_ARRAY_BUFFER, screenVBO);
        glBufferData(GL_ARRAY_BUFFER, quadVertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glCullFace(GL_BACK);

        try {
            defaultShader.createUniform("ModelMat");
            defaultShader.createUniform("ViewMat");
            defaultShader.createUniform("ProjMat");

            screenShader.createUniform("screenTexture");
            screenShader.setUniform("screenTexture", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(this.handle)) {
            glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glEnable(GL_DEPTH_TEST);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            model.render(defaultShader);

            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glDisable(GL_DEPTH_TEST);
            glClear(GL_COLOR_BUFFER_BIT);

            render(screenShader, screenVAO);

            // IMGUI START
            imGuiGlfw.newFrame();
            ImGui.newFrame();

            imguiLayer.imgui();

            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                final long backupWindowPtr = glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                GLFW.glfwMakeContextCurrent(backupWindowPtr);
            }
            //IMGUI END

            update();
        }

        // Cleanup: Delete VAO and VBO after the loop ends
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteProgram(defaultShader.getId());
        glDeleteProgram(screenShader.getId());

        glDeleteVertexArrays(screenVAO);
        glDeleteBuffers(screenVBO);

        glDeleteFramebuffers(fbo);

        //Obscura.getModelLoader().cleanup();
    }

    private void update() {
        glfwSwapBuffers(this.handle);
        glfwPollEvents();
    }

    public static void render(ShaderProgram shaderProgram, int vao) {
        glUseProgram(shaderProgram.getId());
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);
    }

    private static Vector3f position = new Vector3f(0, 0, -5);
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

    public long getHandle() {
        return this.handle;
    }

    public void onKeyCallback(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        }
        this.keyCallback.invoke(window, key, scancode, action, mods);
    }

    private void onFramebufferSizeCallback(long window, int framebufferWidth, int framebufferHeight) {
        this.width = framebufferWidth;
        this.height = framebufferHeight;
        glViewport(0, 0, framebufferWidth, framebufferHeight);

        this.framebufferSizeCallback.invoke(window, framebufferWidth, framebufferHeight);
    }



    public static class Builder {
        private String windowTitle = "Obscura";
        private int windowWidth = 800;
        private int windowHeight = 600;
        private GLFWKeyCallbackI keyCallback = (window, key, scancode, action, mods) -> {};
        private GLFWFramebufferSizeCallbackI framebufferSizeCallback = (window, width, height) -> {};

        public Builder() {
        }

        public Builder setTitle(String title) {
            this.windowTitle = title;
            return this;
        }

        public Builder setWindowSize(int width, int height) {
            this.windowWidth = width;
            this.windowHeight = height;
            return this;
        }

        public Builder addKeyCallback(GLFWKeyCallbackI keyCallback) {
            this.keyCallback = keyCallback;
            return this;
        }

        public Builder addFramebufferSizeCallback(GLFWFramebufferSizeCallbackI framebufferSizeCallback) {
            this.framebufferSizeCallback = framebufferSizeCallback;
            return this;
        }

        public Window build() {
            Window window = new Window(windowTitle, windowWidth, windowHeight);
            window.keyCallback = keyCallback;
            window.framebufferSizeCallback = framebufferSizeCallback;
            return window;
        }
    }
}
