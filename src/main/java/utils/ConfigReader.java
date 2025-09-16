package utils;

import java.io.InputStream;
import java.util.Properties;

// ConfigReader loads properties from src/test/resources/config.properties
public class ConfigReader {
    private static final Properties props = new Properties();

    static {
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            props.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // get: system property overrides file property
    public static String get(String key, String defaultVal) {
        return System.getProperty(key, props.getProperty(key, defaultVal));
    }
}
