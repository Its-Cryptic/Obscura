package dev.cryptic.obscura;

import org.apache.logging.log4j.*;

public class Obscura {
    public static final Logger LOGGER = LogManager.getLogger(Obscura.class.getSimpleName());

    public static void main(String[] args) {
        LOGGER.error("Starting Obscura");
    }

}