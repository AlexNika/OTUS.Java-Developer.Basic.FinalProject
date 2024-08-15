package ru.alexnika.faker.http.server.response.processors;

import ru.alexnika.faker.http.server.request.HttpMethod;
import ru.alexnika.faker.http.server.request.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.jetbrains.annotations.NotNull;

public class OptionsProcessor extends Processor {

    @Override
    public void execute(HttpRequest request, @NotNull OutputStream out) throws IOException {
        String datetime = LocalDateTime.now().toString();
        String methods = HttpMethod.getAllMethods();
        logger.debug("methods: {}", methods);
        String response = templateRequest.prepareOptionsResponse(200, "OK", datetime, methods,
                "http://localhost:3391", methods,
                "Origin, Content-Type, Accept");
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("I/O error occurs", e);
        }
    }
}
