package dev.cryptic.obscura;

import dev.cryptic.obscura.core.HelloWorld;
import dev.cryptic.obscura.core.ResourceLocation;
import dev.cryptic.obscura.core.Window;
import dev.cryptic.obscura.core.render.shader.Shader;
import dev.cryptic.obscura.core.render.shader.ShaderProgram;
import dev.cryptic.obscura.core.render.shader.ShaderType;
import dev.cryptic.obscura.logging.LogUtils;
import org.apache.logging.log4j.*;

public class Obscura {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static Window window;
    public static void main(String[] args) {
        LOGGER.info("Starting Obscura");
        //window = new Window("Obscura", 1280, 720, true);
        //window.init();
        HelloWorld helloWorld = new HelloWorld(1280, 720);
        helloWorld.run();
        LOGGER.info("Finished Obscura");
    }

}