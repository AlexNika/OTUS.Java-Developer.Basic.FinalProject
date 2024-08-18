package ru.alexnika.faker.http.server.response.processors;

import ru.alexnika.faker.http.server.request.HttpAccept;
import ru.alexnika.faker.http.server.request.HttpRequest;
import ru.alexnika.faker.http.server.response.HttpResponse;
import ru.alexnika.faker.http.server.response.Response;

import java.io.OutputStream;

import org.jetbrains.annotations.NotNull;

public class DefaultNotFoundRequestProcessor extends Processor {

    @Override
    public void execute(@NotNull HttpRequest request, @NotNull OutputStream out) {
        logger.info("DefaultNotFoundRequest processor executed");
        request.info();
        HttpAccept acceptType = request.getAcceptType();
        Response httpresponse = HttpResponse.error404(acceptType);
        String response = templateRequest.prepareResponse(httpresponse);
        send(out, response);
    }
}
