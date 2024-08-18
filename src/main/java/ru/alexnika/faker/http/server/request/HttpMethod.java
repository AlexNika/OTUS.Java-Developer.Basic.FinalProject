package ru.alexnika.faker.http.server.request;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public enum HttpMethod {
    GET(true),
    HEAD(true),
    POST(true),
    PUT(true),
    PATCH(false),
    DELETE(true),
    OPTIONS(true);

    public static final int MAX_LENGTH;
    public final boolean isALLOWED;
    private static final Iterator<HttpMethod> httpMethodValues = Arrays.stream(values()).iterator();
    private static final Logger logger = LogManager.getLogger(HttpMethod.class.getName());

    HttpMethod(boolean isALLOWED) {
        this.isALLOWED = isALLOWED;
    }

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

    public static @NotNull String getAllMethods() {
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
