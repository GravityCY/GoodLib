package me.gravityio.yaclutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YaclUtils {
    public static final String MOD_ID = "goodlib-yacl-utils";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean DEBUG = false;

    public static void DEBUG(String message, Object... objects) {
        if (!DEBUG) return;
        LOGGER.info(message, objects);
    }

}
