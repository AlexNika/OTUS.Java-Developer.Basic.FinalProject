package ru.alexnika.faker.http.server.processors;

import org.jetbrains.annotations.NotNull;
import ru.alexnika.faker.http.server.requestanalyzer.HttpRequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultNotFoundRequestProcessor implements RequestProcessor {
    private static final Logger logger = LogManager.getLogger(DefaultNotFoundRequestProcessor.class.getName());

    @Override
    public void execute(@NotNull HttpRequestParser request, @NotNull OutputStream out) {
        request.info();
        TemplateRequestPreprocessor templateRequest = new TemplateRequestPreprocessor();
        String responseBody = "<html><body><h1>Page Not Found</h1></body></html>";
        String response = templateRequest.prepareResponse(404, "Page Not Found",
                "text/html", responseBody);
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("I/O error occurs", e);
        }
    }
}
