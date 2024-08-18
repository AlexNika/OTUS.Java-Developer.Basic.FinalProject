package ru.alexnika.faker.http.server.response.processors;

import ru.alexnika.faker.http.server.request.HttpAccept;
import ru.alexnika.faker.http.server.request.HttpRequest;
import ru.alexnika.faker.http.server.response.HttpResponse;
import ru.alexnika.faker.http.server.response.Response;

import java.io.OutputStream;
import org.jetbrains.annotations.NotNull;

public class HomePageRequestProcessor extends Processor {

    @Override
    public void execute(@NotNull HttpRequest request, @NotNull OutputStream out) {
        logger.info("Homepage processor executed");
        HttpAccept acceptType = request.getAcceptType();
        String responseBody = "<html><body><h1>AlexNika's FAKER server answering...</h1><h2>Good bye!</h2></body></html>";
        Response httpresponse = HttpResponse.ok(acceptType, responseBody);
        String response = templateRequest.prepareResponse(httpresponse);
        send(out, response);
    }
}
