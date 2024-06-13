package dev.cryptic.obscura.core.utils;

import dev.cryptic.obscura.Obscura;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ResourceLocation {

    private final String path;

    public ResourceLocation(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static ResourceLocation create(String path) {
        return new ResourceLocation(path);
    }

    public static ResourceLocation asset(String path) {
        return ResourceFolder.ASSETS.resource(path);
    }

    public static ResourceLocation texture(String path) {
        return ResourceFolder.TEXTURES.resource(path);
    }

    public static ResourceLocation shader(String path) {
        return ResourceFolder.SHADERS.resource(path);
    }

    public String openAsString() throws Exception {
        String result;
        try (InputStream in = Utils.class.getResourceAsStream("/"+path)) {
            if (in == null) throw new Exception("Resource not found: " + path);
            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8);
            result = scanner.useDelimiter("\\A").next();
            Obscura.LOGGER.info("Loaded resource: " + path);
            return result;
        }
    }

    public enum ResourceFolder {
        ASSETS("assets"),
        TEXTURES(ASSETS.getLocation() + "/textures"),
        SHADERS(ASSETS.getLocation() + "/shaders");

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
