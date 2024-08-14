package ru.alexnika.faker.http.server.requestanalyzer;

import ru.alexnika.faker.http.server.exceptions.BadRequestException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum HttpProtocol {
    HTTP_0_9("HTTP/0.9", 0 , 9, false),
    HTTP_1_0("HTTP/1.0", 1 , 0, false),
    HTTP_1_1("HTTP/1.1", 1 , 1, true),
    HTTP_2_0("HTTP/2.0", 2 , 0, false),
    HTTP_3_0("HTTP/3.0", 3 , 0, false);

    public final String LITERAL;
    public final int MAJOR;
    public final int MINOR;
    public final boolean isSUPPORTED;
    private static final Logger logger = LogManager.getLogger(HttpProtocol.class.getName());
    private static final Pattern httpVersionRegexPattern = Pattern.compile("^HTTP/(?<major>\\d+).(?<minor>\\d+)");

    HttpProtocol(String LITERAL, int MAJOR, int MINOR, boolean isSUPPORTED) {
        this.LITERAL = LITERAL;
        this.MAJOR = MAJOR;
        this.MINOR = MINOR;
        this.isSUPPORTED = isSUPPORTED;
    }

    public static HttpProtocol getBestCompatibleProtocol(String literalProtocol) {
        Matcher matcher = httpVersionRegexPattern.matcher(literalProtocol);
        if (!matcher.find() || matcher.groupCount() != 2) {
            throw new BadRequestException("Best compatible HTTP protocol version not found");
        }
        int major = Integer.parseInt(matcher.group("major"));
        int minor = Integer.parseInt(matcher.group("minor"));
        HttpProtocol bestCompatibleHttpProtocol = null;
        for (HttpProtocol p : HttpProtocol.values()) {
            if (p.LITERAL.equalsIgnoreCase(literalProtocol)) {
                return p;
            } else {
                if (p.MAJOR == major) {
                    if (p.MINOR <= minor) {
                        bestCompatibleHttpProtocol = p;
                    }
                }
            }
        }
        return bestCompatibleHttpProtocol;
    }


}
