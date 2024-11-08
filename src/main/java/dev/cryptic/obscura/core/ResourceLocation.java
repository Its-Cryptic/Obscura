package dev.cryptic.obscura.core;

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

    public String open() {
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
