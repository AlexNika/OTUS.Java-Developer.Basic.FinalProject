package ru.alexnika.faker.http.server.processors;

import org.jetbrains.annotations.NotNull;
import ru.alexnika.faker.http.server.requestanalyzer.HttpRequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileRequestProcessor implements RequestProcessor {
    private static final Logger logger = LogManager.getLogger(FileRequestProcessor.class.getName());

    @Override
    public void execute(HttpRequestParser request, @NotNull OutputStream out) throws IOException {
        try {
            String fileContent = Files.readString(Path.of("static/import.this"));
            logger.info("The file found in 'static' folder");
            String response = "";
            response = response +
                    "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html\r\n" +
                    "\r\n" +
                    fileContent;
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.warn("Requested file not found");
        }
    }
}
