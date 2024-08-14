package ru.alexnika.faker.http.server.processors;

import ru.alexnika.faker.http.server.requestanalyzer.HttpRequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class DefaultInternalServerErrorRequestProcessor implements RequestProcessor {
    private static final Logger logger = LogManager.getLogger(DefaultInternalServerErrorRequestProcessor.class.getName());

    @Override
    public void execute(HttpRequestParser request, @NotNull OutputStream out) throws IOException {
        TemplateRequestPreprocessor templateRequest = new TemplateRequestPreprocessor();
        String responseBody = "<html><body><h1>INTERNAL SERVER ERROR</h1></body></html>";
        String response = templateRequest.prepareResponse(500, "Internal Server Error",
                "text/html", responseBody);
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("I/O error occurs", e);
        }
    }
}
