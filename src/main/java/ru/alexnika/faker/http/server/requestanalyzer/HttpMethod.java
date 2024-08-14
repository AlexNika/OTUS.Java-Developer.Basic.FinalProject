package ru.alexnika.faker.http.server.requestanalyzer;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum HttpMethod {
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    OPTIONS;

    public static final int MAX_LENGTH;
    private static final Iterator<HttpMethod> httpMethodValues = Arrays.stream(values()).iterator();
    private static final Logger logger = LogManager.getLogger(HttpMethod.class.getName());

    static {
        int tempMaxLength = -1;
        for (HttpMethod method : values()) {
            if (method.name().length() > tempMaxLength) {
                tempMaxLength = method.name().length();
            }
        }
        MAX_LENGTH = tempMaxLength;
        logger.debug("MAX_LENGTH of methods name: {}", MAX_LENGTH);
    }

    public static String getAllMethods() {
        StringBuilder methodsSB = new StringBuilder();
        int count = 0;
        while(httpMethodValues.hasNext()) {
            methodsSB.append(httpMethodValues.next());
            if (count < values().length - 1) {
                methodsSB.append(", ");
                count++;
            }
        }
        logger.debug("All available HTTP methods: {}", methodsSB.toString());
        return methodsSB.toString();
    }
}
