package me.gravityio.goodlib.lib;

import java.util.HashMap;
import java.util.Map;

public class MissingTranslation {
    public static Map<String, String> cache = new HashMap<>();

    public static String get(String key) {
        return cache.get(key);
    }

    public static String add(String key, String value) {
        cache.put(key, value);
        return value;
    }

}
