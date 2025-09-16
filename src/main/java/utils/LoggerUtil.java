package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {

    public static void info(Class<?> clazz, String message) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info(message);
    }

    public static void debug(Class<?> clazz, String message) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.debug(message);
    }

    public static void warn(Class<?> clazz, String message) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.warn(message);
    }

    public static void error(Class<?> clazz, String message, Throwable t) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.error(message, t);
    }
}


