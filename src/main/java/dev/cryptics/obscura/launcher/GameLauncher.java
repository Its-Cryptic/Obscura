package dev.cryptics.obscura.launcher;

import dev.cryptics.obscura.Obscura;
import dev.cryptics.obscura.config.ObscuraContext;
import dev.cryptics.obscura.core.AbstractGame;
import org.reflections.Reflections;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.List;

public class GameLauncher {
    public static AbstractGame launchGame(ObscuraContext context) {
        Class<? extends AbstractGame> gameClass = getGame();
        if (gameClass == null) {
            throw new IllegalStateException("No @ObscuraGame class found in package: " + System.getProperty("game.package", ""));
        }
        return createGameInstance(gameClass);
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
                return constructor.newInstance(Obscura.getContext());
            } else {
                throw new IllegalStateException("Invalid constructor parameters for @ObscuraGame class: " + gameClass.getName());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create game instance", e);
        }
    }
}
