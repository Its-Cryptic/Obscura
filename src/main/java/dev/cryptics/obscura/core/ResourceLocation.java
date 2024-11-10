package dev.cryptics.obscura.core;

import dev.cryptics.obscura.core.render.shader.ShaderType;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ResourceLocation {
    private String path;

    public ResourceLocation(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static ResourceLocation of(String path) {
        return new ResourceLocation(path);
    }

    public static ResourceLocation shader(String path) {
        return ResourceFolder.SHADERS.resource(path);
    }

    public static ResourceLocation shader(String path, ShaderType shaderType) {
        return shader(path + "." + shaderType.getFileExtension());
    }

    public static ResourceLocation texture(String path) {
        return ResourceFolder.TEXTURES.resource(path);
    }

    public static ResourceLocation model(String path) {
        return ResourceFolder.MODELS.resource(path);
    }

    public String openAsString() {
        String result;
        try (InputStream stream = ResourceLocation.class.getResourceAsStream("/" + path)) {
            if (stream == null) throw new RuntimeException("Resource not found: " + path);
            Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8);
            result = scanner.useDelimiter("\\A").next();
        } catch (Exception e) {
            throw new RuntimeException("Failed to open resource: " + path, e);
        }
        return result;
    }

    public InputStream open() {
        InputStream stream = ResourceLocation.class.getResourceAsStream("/" + path);
        if (stream == null) throw new RuntimeException("Resource not found: " + path);
        return stream;
    }

    public enum ResourceFolder {
        ASSETS("assets/"),
        TEXTURES(ASSETS.getLocation() + "textures"),
        SHADERS(ASSETS.getLocation() + "shaders"),
        MODELS(ASSETS.getLocation() + "models");

        private final String location;

        ResourceFolder(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }

        public ResourceLocation resource(String path) {
            return new ResourceLocation(location + "/" + path);
        }
    }
}
