package ru.alexnika.faker.http.server.response;

import org.jetbrains.annotations.NotNull;
import ru.alexnika.faker.http.server.response.processors.TemplateRequestPreprocessor;
import ru.alexnika.faker.http.server.request.HttpAccept;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static ru.alexnika.faker.http.server.response.HttpStatusCode.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpResponse {
    private static final Logger logger = LogManager.getLogger(HttpResponse.class.getName());

    public static @NotNull Response ok(HttpAccept acceptType, String responseBody) {
        return new Response(200, "OK", acceptType, responseBody);
    }

    public static @NotNull Response ok(HttpAccept acceptType) {
        return new Response(200, "OK", acceptType);
    }

    public static @NotNull Response create(HttpAccept acceptType, String responseBody) {
        return new Response(201, "Created", acceptType, responseBody);
    }

    public static @NotNull Response noContent(HttpAccept acceptType) {
        return new Response(204, "No content", acceptType);
    }

    public static @NotNull Response error400(HttpAccept acceptType, String responseBody) {
        return new Response(CLIENT_ERROR_400_BAD_REQUEST.STATUS_CODE,
                CLIENT_ERROR_400_BAD_REQUEST.MESSAGE, acceptType, responseBody);
    }

    public static @NotNull Response error405(HttpAccept acceptType, String responseBody) {
        return new Response(CLIENT_ERROR_405_METHOD_NOT_ALLOWED.STATUS_CODE,
                CLIENT_ERROR_405_METHOD_NOT_ALLOWED.MESSAGE, acceptType, responseBody);
    }

    public static @NotNull Response error404(HttpAccept acceptType) {
        String responseBody = "<html><body><h1>404 Page Not Found</h1></body></html>";
        return new Response(CLIENT_ERROR_404_NOT_FOUND.STATUS_CODE,
                CLIENT_ERROR_404_NOT_FOUND.MESSAGE, acceptType, responseBody);
    }

    public static @NotNull Response error406(HttpAccept acceptType) {
        String responseBody = "<html><body><h1>406 Not Acceptable</h1></body></html>";
        return new Response(CLIENT_ERROR_406_NOT_ACCEPTABLE.STATUS_CODE,
                CLIENT_ERROR_406_NOT_ACCEPTABLE.MESSAGE, acceptType, responseBody);
    }

    public static @NotNull Response error414(HttpAccept acceptType) {
        String responseBody = "<html><body><h1>414 URI Too Long</h1></body></html>";
        return new Response(CLIENT_ERROR_414_URI_TOO_LONG.STATUS_CODE,
                CLIENT_ERROR_414_URI_TOO_LONG.MESSAGE, acceptType, responseBody);
    }

    public static @NotNull Response error500(HttpAccept acceptType) {
        String responseBody = "<html><body><h1>500 INTERNAL SERVER ERROR</h1></body></html>";
        return new Response(SERVER_ERROR_500_INTERNAL_SERVER_ERROR.STATUS_CODE,
                SERVER_ERROR_500_INTERNAL_SERVER_ERROR.MESSAGE, acceptType, responseBody);
    }

    public static @NotNull Response error501(HttpAccept acceptType) {
        String responseBody = "<html><body><h1>501 Not implemented</h1></body></html>";
        return new Response(SERVER_ERROR_501_NOT_IMPLEMENTED.STATUS_CODE,
                SERVER_ERROR_500_INTERNAL_SERVER_ERROR.MESSAGE, acceptType, responseBody);
    }

    public static @NotNull Response error505(HttpAccept acceptType) {
        String responseBody = "<html><body><h1>505 HTTP Version Not Supported</h1></body></html>";
        return new Response(SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED.STATUS_CODE,
                SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED.MESSAGE, acceptType, responseBody);
    }

    public static void sendResponse(Response httpresponse, @NotNull OutputStream out) {
        TemplateRequestPreprocessor templateRequest = new TemplateRequestPreprocessor();
        String response = templateRequest.prepareResponse(httpresponse);
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("I/O error occurs", e);
        }
    }
}
