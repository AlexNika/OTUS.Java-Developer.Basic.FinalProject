package ru.alexnika.faker.http.server.response.processors;

import org.jetbrains.annotations.NotNull;
import ru.alexnika.faker.http.server.request.HttpAccept;
import ru.alexnika.faker.http.server.request.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import ru.alexnika.faker.http.server.response.HttpResponse;
import ru.alexnika.faker.http.server.response.Response;

public class DefaultNotFoundRequestProcessor extends Processor {

    @Override
    public void execute(@NotNull HttpRequest request, @NotNull OutputStream out) {
        logger.info("DefaultNotFoundRequest processor executed");
        request.info();
        HttpAccept acceptType = request.getAcceptType();
        Response httpresponse = HttpResponse.error404(acceptType);
        String response = templateRequest.prepareResponse(httpresponse);
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("I/O error occurs", e);
        }
    }
}
