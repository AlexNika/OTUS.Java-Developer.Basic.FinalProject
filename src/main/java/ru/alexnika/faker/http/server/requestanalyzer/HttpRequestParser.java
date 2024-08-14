package ru.alexnika.faker.http.server.requestanalyzer;

import static ru.alexnika.faker.http.server.requestanalyzer.HttpStatusCode.*;

import ru.alexnika.faker.http.server.config.Config;
import ru.alexnika.faker.http.server.exceptions.BadRequestException;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("FieldMayBeFinal")
public class HttpRequestParser {
    private static final Logger logger = LogManager.getLogger(HttpRequestParser.class.getName());
    private String rawRequest;
    private StringBuilder rawRequestSB;
    private String uri;
    private HttpMethod method;
    private HttpProtocol protocol;
    private Map<String, String> requestParams;
    private Map<String, String> headers;
    private String body;

    public String getRoutingKey() {
        return method + " " + uri;
    }

    public HttpRequestParser(String rawRequest) {
        this.rawRequest = rawRequest;
        this.rawRequestSB = new StringBuilder(this.rawRequest);
        this.requestParams = new HashMap<>();
        this.headers = new HashMap<>();
        this.parse();
    }

    public String getBody() {
        return body;
    }

    public boolean containsParameter(String key) {
        return requestParams.containsKey(key);
    }

    public String getParameter(String key) {
        return requestParams.get(key);
    }

    public void info() {
        logger.info("uri: {}", uri);
        logger.info("http method: {}", method);
        logger.info("http protocol: {}", protocol);
        if (!(body == null)) {
            logger.info("body: {}", body);
        } else {
            logger.info("body: null");
        }
        if (!(requestParams == null) && !requestParams.isEmpty()) {
            for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                logger.info("uri parameters: {}: {}", entry.getKey(), entry.getValue());
            }
        }
        if (!(headers == null) && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                logger.info("headers: {}: {}", entry.getKey(), entry.getValue());
            }
        }
        logger.debug("rawRequest:\n{}", rawRequest);
    }

    private void parse() {
        if (this.rawRequestSB == null || this.rawRequestSB.isEmpty()) {
            logger.error("HTTP request is empty");
            throw new BadRequestException("HTTP request is empty");
        }
        parseRequestLine();
        parseMessageHeader();
        parseEntityBody();
    }

    private void parseRequestLine() {
        int uriMaxLength = getUriMaxLengthFromConfig();
        String[] requestLineParts;
        String abemptyUri = "/";
        int beginIndex = 0;
        int endIndex = this.rawRequestSB.indexOf("\r\n", beginIndex);
        String requestLine = this.rawRequestSB.substring(beginIndex, endIndex);
        requestLineParts = requestLine.split(" ");
        getMethod(requestLineParts[0]);
        if (requestLineParts.length == 2) {
            logger.info("URI is absolute empty. Set default URI as '/'");
            this.uri = abemptyUri;
            getProtocol(requestLineParts[1]);
            return;
        } else if (requestLineParts.length == 3) {
            this.uri = requestLineParts[1];
            if (this.uri.length() > uriMaxLength) {
                logger.error("{}: '{}'", CLIENT_ERROR_414_BAD_REQUEST.MESSAGE, requestLineParts[1]);
                throw new BadRequestException(CLIENT_ERROR_414_BAD_REQUEST.STATUS_CODE +
                        CLIENT_ERROR_414_BAD_REQUEST.MESSAGE);
            }
            getProtocol(requestLineParts[2]);
        } else {
            logger.error("{}: '{}'", CLIENT_ERROR_400_BAD_REQUEST.MESSAGE, requestLine);
            throw new BadRequestException(CLIENT_ERROR_400_BAD_REQUEST.STATUS_CODE +
                    CLIENT_ERROR_400_BAD_REQUEST.MESSAGE);
        }
        getRequestParams();
        rawRequestSB.delete(beginIndex, endIndex);
    }

    private void parseMessageHeader() {
        logger.debug("from parseMessageHeader. -> rawRequestSB length = {}", rawRequestSB.length());
        int beginIndex = 0;
        int endIndex = this.rawRequestSB.indexOf("\r\n\r\n");
        String rawMessageHeader = this.rawRequestSB.substring(beginIndex, endIndex);
        if (rawMessageHeader.isEmpty()) {
            logger.error("{}: '{}'", CLIENT_ERROR_400_BAD_REQUEST.MESSAGE, rawMessageHeader);
            throw new BadRequestException(CLIENT_ERROR_400_BAD_REQUEST.STATUS_CODE +
                    CLIENT_ERROR_400_BAD_REQUEST.MESSAGE);
        }
        String[] rawMessageHeaderLines = rawMessageHeader.split("\r\n");
        for (String o : rawMessageHeaderLines) {
            if (!o.contains(": ")) {
                continue;
            }
            String[] keyValue = o.split(": ", 2);
            this.headers.put(keyValue[0], keyValue[1]);
        }
        rawRequestSB.delete(beginIndex, endIndex + 4);
    }

    private void parseEntityBody() {
        int beginIndex = 0;
        if (rawRequestSB.isEmpty()) {
            return;
        }
        if (this.method == HttpMethod.POST || this.method == HttpMethod.PUT) {
            body = rawRequestSB.substring(beginIndex);
        }
    }

    private void getRequestParams() {
        String[] uriParts;
        if (!this.uri.contains("?")) {
            return;
        }
        uriParts = this.uri.split("[?]");
        this.uri = uriParts[0];
        logger.debug("uriParts = {}", Arrays.toString(uriParts));
        String[] keyValue;
        if (!uriParts[1].contains("&")) {
            keyValue = uriParts[1].split("=");
            this.requestParams.put(keyValue[0], keyValue[1]);
            return;
        }
        String[] keysValues = uriParts[1].split("&");
        for (String o : keysValues) {
            keyValue = o.split("=");
            this.requestParams.put(keyValue[0], keyValue[1]);
        }
    }

    private void getMethod(String requestLinePart) {
        try {
            method = HttpMethod.valueOf(requestLinePart);
        } catch (BadRequestException e) {
            logger.error("{}: '{}'", CLIENT_ERROR_401_METHOD_NOT_ALLOWED.MESSAGE, requestLinePart);
            throw new BadRequestException(CLIENT_ERROR_401_METHOD_NOT_ALLOWED.STATUS_CODE +
                    CLIENT_ERROR_401_METHOD_NOT_ALLOWED.MESSAGE);
        }
    }

    private void getProtocol(String requestLinePart) {
        try {
            protocol = HttpProtocol.getBestCompatibleProtocol(requestLinePart);
            if (!protocol.isSUPPORTED) {
                logger.error("{}: '{}'", SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED.MESSAGE, protocol.LITERAL);
                throw new BadRequestException(SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED.STATUS_CODE +
                        SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED.MESSAGE);
            }
        } catch (BadRequestException e) {
            logger.error("Best compatible HTTP protocol version not found");
            throw new BadRequestException("Best compatible HTTP protocol version not found");
        }
    }

    private int getUriMaxLengthFromConfig() {
        int uriMaxLength = 2046;
        try {
            uriMaxLength = Integer.parseInt(Config.getProperty("uri.maxLength"));
        } catch (NumberFormatException e) {
            logger.warn("Parameter 'uri.maxLength' in file 'config.properties' must be a string consisting only of digits", e);
            logger.warn("The default 'uri.maxLength' value is 2046");
        }
        return uriMaxLength;
    }
}