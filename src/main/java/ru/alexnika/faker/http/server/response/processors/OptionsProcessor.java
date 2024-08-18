package ru.alexnika.faker.http.server.response.processors;

import ru.alexnika.faker.http.server.request.HttpMethod;
import ru.alexnika.faker.http.server.request.HttpRequest;

import java.io.OutputStream;
import java.time.LocalDateTime;

import org.jetbrains.annotations.NotNull;

public class OptionsProcessor extends Processor {

    @Override
    public void execute(HttpRequest request, @NotNull OutputStream out) {
        String datetime = LocalDateTime.now().toString();
        String methods = HttpMethod.getAllMethods();
        String response = templateRequest.prepareOptionsResponse(200, "OK", datetime, methods,
                "http://localhost:3391", methods,
                "Origin, Content-Type, Accept");
        send(out, response);
    }
}
