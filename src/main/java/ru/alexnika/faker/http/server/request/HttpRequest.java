package ru.alexnika.faker.http.server.request;

import static ru.alexnika.faker.http.server.response.HttpStatusCode.*;

import ru.alexnika.faker.http.server.config.Config;
import ru.alexnika.faker.http.server.exceptions.BadRequestException;
import ru.alexnika.faker.http.server.response.HttpResponse;
import ru.alexnika.faker.http.server.response.Response;

import java.io.OutputStream;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("FieldMayBeFinal")
public class HttpRequest {
    private static final Logger logger = LogManager.getLogger(HttpRequest.class.getName());
    private String rawRequest;
    private StringBuilder rawRequestSB;
    private String uri;
    private String originalUri;
    private HttpMethod method;
    private HttpProtocol protocol;
    private Map<String, String> requestParams;
    private Map<String, String> headers;
    private String body;
    private HttpAccept acceptType;
    private String contentType;
    private OutputStream out;
    private boolean requestStatus = true;

    public String getRoutingKey() {
        return method + " " + uri;
    }

    public HttpRequest(String rawRequest, OutputStream outputStream) {
        this.rawRequest = rawRequest;
        this.rawRequestSB = new StringBuilder(this.rawRequest);
        this.requestParams = new HashMap<>();
        this.headers = new HashMap<>();
        this.out = outputStream;
        this.parse();
    }

    public String getBody() {
        return this.body;
    }

    public HttpAccept getAcceptType() {
        return this.acceptType;
    }

    public String getContentType() {
        return this.requestParams.get("Content-Type");
    }

    public boolean containsParameter(String key) {
        return this.requestParams.containsKey(key);
    }

    public String getParameter(String key) {
        return this.requestParams.get(key);
    }

    public String getOriginalUri() {
        return this.originalUri;
    }

    public HttpProtocol getProtocol() {
        return this.protocol;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public boolean isRequestStatus() {
        return requestStatus;
    }

    public void info() {
        logger.info("uri: {}", this.uri);
        logger.info("http method: {}", this.method);
        logger.info("http protocol: {}", this.protocol);
        if (!(this.body == null)) {
            logger.info("body: {}", this.body);
        } else {
            logger.info("body: null");
        }
        if (!(this.requestParams == null) && !this.requestParams.isEmpty()) {
            for (Map.Entry<String, String> entry : this.requestParams.entrySet()) {
                logger.info("uri parameters: {}: {}", entry.getKey(), entry.getValue());
            }
        }
        if (!(this.headers == null) && !this.headers.isEmpty()) {
            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
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
        parseAcceptType();
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
                requestStatus = false;
                logger.error("{}: '{}'", CLIENT_ERROR_414_URI_TOO_LONG.MESSAGE, requestLineParts[1]);
                Response response = HttpResponse.error414(HttpAccept.ANY);
                HttpResponse.sendResponse(response, out);
            }
            this.originalUri = this.uri;
            getProtocol(requestLineParts[2]);
        } else {
            logger.error("{}: '{}'", CLIENT_ERROR_400_BAD_REQUEST.MESSAGE, requestLine);
            Response response = HttpResponse.error404(HttpAccept.ANY);
            HttpResponse.sendResponse(response, out);
            throw new BadRequestException(CLIENT_ERROR_400_BAD_REQUEST.STATUS_CODE + " " +
                    CLIENT_ERROR_400_BAD_REQUEST.MESSAGE);
        }
        getRequestParams();
        this.rawRequestSB.delete(beginIndex, endIndex);
    }

    private void parseMessageHeader() {
        logger.debug("from parseMessageHeader. -> rawRequestSB length = {}", rawRequestSB.length());
        int beginIndex = 0;
        int endIndex = this.rawRequestSB.indexOf("\r\n\r\n");
        String rawMessageHeader = this.rawRequestSB.substring(beginIndex, endIndex);
        if (rawMessageHeader.isEmpty()) {
            requestStatus = false;
            logger.error("{}: '{}'", CLIENT_ERROR_400_BAD_REQUEST.MESSAGE, rawMessageHeader);
            Response response = HttpResponse.error400(HttpAccept.ANY, CLIENT_ERROR_400_BAD_REQUEST.MESSAGE);
            HttpResponse.sendResponse(response, out);
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

    private void parseAcceptType() {
        if (this.headers.containsKey("Accept")) {
            try {
                logger.debug("this.headers.get(\"Accept\"): {}", this.headers.get("Accept"));
                acceptType = HttpAccept.getBestCompatibleAcceptType(this.headers.get("Accept"));
            } catch (BadRequestException e) {
                requestStatus = false;
                logger.error("{}: '{}'", CLIENT_ERROR_406_NOT_ACCEPTABLE.MESSAGE, acceptType);
                Response response = HttpResponse.error406(HttpAccept.ANY);
                HttpResponse.sendResponse(response, out);
                throw new BadRequestException(CLIENT_ERROR_406_NOT_ACCEPTABLE.STATUS_CODE +
                        CLIENT_ERROR_406_NOT_ACCEPTABLE.MESSAGE);
            }
        } else {
            acceptType = HttpAccept.ANY;
        }
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
            if (keyValue.length != 2) {
                requestStatus = false;
                logger.error("{}: '{}'", CLIENT_ERROR_400_BAD_REQUEST.MESSAGE, uri);
                Response response = HttpResponse.error400(HttpAccept.ANY, CLIENT_ERROR_400_BAD_REQUEST.MESSAGE);
                HttpResponse.sendResponse(response, out);
            }
            logger.debug("keyValue: {},{}", keyValue[0], keyValue[1]);
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
            if (!method.isALLOWED) {
                requestStatus = false;
                Response response = HttpResponse.error405(HttpAccept.ANY, CLIENT_ERROR_405_METHOD_NOT_ALLOWED.MESSAGE);
                HttpResponse.sendResponse(response, out);
            }
        } catch (BadRequestException e) {
            logger.error("{}: '{}'", SERVER_ERROR_501_NOT_IMPLEMENTED.MESSAGE, requestLinePart);
            throw new BadRequestException(SERVER_ERROR_501_NOT_IMPLEMENTED.STATUS_CODE + " " +
                    SERVER_ERROR_501_NOT_IMPLEMENTED.MESSAGE);
        }
    }

    private void getProtocol(String requestLinePart) {
        try {
            protocol = HttpProtocol.getBestCompatibleProtocol(requestLinePart);
            if (!protocol.isSUPPORTED) {
                requestStatus = false;
                logger.error("{}: '{}'", SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED.MESSAGE, protocol.LITERAL);
                Response response = HttpResponse.error505(HttpAccept.ANY);
                HttpResponse.sendResponse(response, out);
            }
        } catch (BadRequestException e) {
            logger.error("Best compatible HTTP protocol version not found", e);
            throw new BadRequestException(SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED.STATUS_CODE + " " +
                    SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED.MESSAGE);
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