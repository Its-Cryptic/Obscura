package dev.cryptic.obscura;

import dev.cryptic.obscura.config.ObscuraContext;
import dev.cryptic.obscura.core.AbstractGame;
import dev.cryptic.obscura.core.ObscuraGame;
import dev.cryptic.obscura.core.Window;
import org.apache.logging.log4j.*;
import org.reflections.Reflections;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Set;

public class Obscura {
    public static final Logger LOGGER = LogManager.getLogger();
    private static Window window;
    private static ObscuraContext context;
    private static AbstractGame game;
    public static void main(String[] args) {
        LOGGER.info("Starting Obscura");
        context = new ObscuraContext(args);
        launchGame(context);
        game.start();

        LOGGER.info("Finished Obscura");
    }

    public static void setWindow(Window window) {
        Obscura.window = window;
    }

    public static Window getWindow() {
        return window;
    }

    public static void launchGame(ObscuraContext context) {
        Class<? extends AbstractGame> gameClass = getGame();
        if (gameClass == null) {
            throw new IllegalStateException("No @ObscuraGame class found in package: " + System.getProperty("game.package", ""));
        }
        game = createGameInstance(gameClass);
    }

    @Nullable
    private static Class<? extends AbstractGame> getGame() {
        String packageName = System.getProperty("game.package", "");
        Reflections reflections = packageName.isEmpty() ? new Reflections() : new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(ObscuraGame.class)
                .stream()
                .filter(AbstractGame.class::isAssignableFrom)
                .map(aClass -> (Class<? extends AbstractGame>) aClass)
                .findFirst()
                .orElse(null);
    }

    private static <T extends AbstractGame> T createGameInstance(Class<T> gameClass) {
        try {
            Constructor<T> constructor = gameClass.getDeclaredConstructor(ObscuraContext.class);
            List<Parameter> parameters = List.of(constructor.getParameters());
            if (parameters.isEmpty()) {
                return constructor.newInstance();
            } else if (parameters.size() == 1 && parameters.get(0).getType().equals(ObscuraContext.class)) {
                return constructor.newInstance(context);
            } else {
                throw new IllegalStateException("Invalid constructor parameters for @ObscuraGame class: " + gameClass.getName());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create game instance", e);
        }
    }

}