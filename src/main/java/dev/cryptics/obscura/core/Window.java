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
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window implements Runnable {
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

    @Override
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        initWindow();
        Obscura.getContext().getRenderer().init();
        initImgui();

        initCamera();

        loop();

        Obscura.getContext().getRenderer().cleanup();
        Obscura.getModelLoader().cleanup();

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

        glfwMakeContextCurrent(handle);
        // Enable v-sync
        glfwSwapInterval(1);

        glfwShowWindow(handle);
        GL.createCapabilities();
    }

    public void initCamera() {
        Camera camera = Obscura.getContext().getRenderer().getMainCamera();
        camera.updateViewMatrix();
        camera.updateProjectionMatrix();
    }

    private void loop() {
        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        while (!glfwWindowShouldClose(this.handle)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            MatrixStack matrixStack = new MatrixStack();
            Obscura.getContext().getRenderer().render(matrixStack);
            if (!matrixStack.clear())
                LOGGER.error("Matrix stack is not clear!");


            renderImgui();

            update();
        }
    }

    private void update() {
        glfwSwapBuffers(this.handle);
        glfwPollEvents();
    }

    public void renderImgui() {
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
        Obscura.getContext().getRenderer().getMainCamera().updateProjectionMatrix();
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
