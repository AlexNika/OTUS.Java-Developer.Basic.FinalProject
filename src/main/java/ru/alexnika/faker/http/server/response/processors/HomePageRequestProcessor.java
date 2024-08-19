package ru.alexnika.faker.http.server.response.processors;

import ru.alexnika.faker.http.server.request.HttpAccept;
import ru.alexnika.faker.http.server.request.HttpRequest;
import ru.alexnika.faker.http.server.response.HttpResponse;
import ru.alexnika.faker.http.server.response.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;

public class HomePageRequestProcessor extends Processor {

    @Override
    public void execute(@NotNull HttpRequest request, @NotNull OutputStream out) {
        logger.info("Homepage processor executed");
        String responseBody;
        try {
            responseBody = Files.readString(Path.of("static/index.html"));
            logger.info("The file found in 'static' folder");
        } catch (IOException e) {
            responseBody = "<html><body><h1>FAKE data server answering...</h1><h2>Welcome!</h2></body></html>";
            logger.warn("Requested file not found");
        }
        String response;
        HttpAccept acceptType = request.getAcceptType();
        Response httpresponse;
        if (responseBody != null) {
            httpresponse = HttpResponse.ok(acceptType, responseBody);
        } else {
            httpresponse = HttpResponse.error404(acceptType);
        }
        response = templateRequest.prepareResponse(httpresponse);
        send(out, response);
    }
}
