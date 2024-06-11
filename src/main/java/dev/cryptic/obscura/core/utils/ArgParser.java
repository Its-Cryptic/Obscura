package dev.cryptic.obscura.core.utils;

public class ArgParser {
    public void parse(String[] args) {
        Context context = new Context();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String[] split = arg.substring(2).split("=");
                String key = split[0];
                String value = split[1];

                switch (key) {
                    case "windowsize":
                        context.size = WindowSizePresets.valueOf(value.toUpperCase());
                        break;
                    case "fullscreen":
                        context.fullscreen = Boolean.parseBoolean(value);
                        break;
                    default:
                        System.out.println("Unknown argument: " + key);
                        break;
                }
            }
        }
    }

    public static class Context {
        public WindowSizePresets size;
        public boolean fullscreen;

        public WindowSizePresets getWindowSize() {
            return size;
        }

        public boolean isFullscreen() {
            return fullscreen;
        }
    }
}
