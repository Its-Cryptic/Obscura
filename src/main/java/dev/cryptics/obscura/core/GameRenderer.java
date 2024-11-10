package dev.cryptics.obscura.core;

import dev.cryptics.obscura.core.render.shader.Shader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class GameRenderer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceLocation, Shader> shaders = new HashMap<>();
    private static final Camera mainCamera = new Camera(90.0f, 0.1f, 1000.0f);
    private static Matrix4f projectionMatrix = new Matrix4f();

    public static void render() {
    }

    public static Camera getMainCamera() {
        return mainCamera;
    }
}
