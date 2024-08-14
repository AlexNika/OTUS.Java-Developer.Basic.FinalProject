package ru.alexnika.faker.http.server.config;

import java.io.*;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {
    private static final Logger logger = LogManager.getLogger(Config.class.getName());
    private static final Properties configProperties = new Properties();

    static {
        try (FileInputStream configInputStream = new FileInputStream("src/main/resources/config.properties")) {
            configProperties.load(configInputStream);
            logger.debug("Configuration properties have been initialized");
        } catch (FileNotFoundException e) {
            logger.error("Config file not found in resources folder", e);
        } catch (IOException e) {
            logger.error("General I/O exception", e);
        }
    }

    public static String getProperty(String key) {
        return configProperties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return configProperties.getProperty(key, defaultValue);
    }
}
