package ru.alexnika.faker.http.server.processors;

import ru.alexnika.faker.http.server.requestanalyzer.HttpMethod;
import ru.alexnika.faker.http.server.requestanalyzer.HttpRequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class OptionsProcessor implements RequestProcessor {
    private static final Logger logger = LogManager.getLogger(OptionsProcessor.class.getName());

    @Override
    public void execute(HttpRequestParser request, @NotNull OutputStream out) throws IOException {
        String datetime = LocalDateTime.now().toString();
        String methods = HttpMethod.getAllMethods();
        logger.debug("methods: {}", methods);
        TemplateRequestPreprocessor templateRequest = new TemplateRequestPreprocessor();
        String response = templateRequest.prepareOptionsResponse(200, "OK", datetime, methods,
                "http://localhost:3391", methods,
                "Origin, Content-Type, Accept");
        out.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
