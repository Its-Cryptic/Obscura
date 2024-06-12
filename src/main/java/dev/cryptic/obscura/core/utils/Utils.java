package dev.cryptic.obscura.core.utils;

import dev.cryptic.obscura.Obscura;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Utils {
    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public static String loadResource(String path) throws Exception {
        String result;
        try (InputStream in = Utils.class.getResourceAsStream(path)) {
            if (in == null) throw new Exception("Resource not found: " + path);
            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8);
            result = scanner.useDelimiter("\\A").next();
            Obscura.LOGGER.info("Loaded resource: " + path);
            Obscura.LOGGER.info("Resource content:\n" + result);
            return result;
        }
    }
}
