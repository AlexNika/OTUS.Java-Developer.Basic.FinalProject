package ru.alexnika.faker.http.server.response.processors;

import ru.alexnika.faker.http.server.request.HttpAccept;
import ru.alexnika.faker.http.server.request.HttpRequest;
import ru.alexnika.faker.http.server.response.HttpResponse;
import ru.alexnika.faker.http.server.response.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;

public class FileRequestProcessor extends Processor {

    @Override
    public void execute(@NotNull HttpRequest request, @NotNull OutputStream out) throws IOException {
        logger.info("FileRequest processor executed");
        String fileContent = null;
        try {
            fileContent = Files.readString(Path.of("static/import.this"));
            logger.info("The file found in 'static' folder");
        } catch (IOException e) {
            logger.warn("Requested file not found");
        }
        String response = "";
        HttpAccept acceptType = request.getAcceptType();
        Response httpresponse;
        if (fileContent != null) {
            httpresponse = HttpResponse.ok(acceptType, fileContent);
        } else {
            httpresponse = HttpResponse.error404(acceptType);
        }
        response = templateRequest.prepareResponse(httpresponse);
        out.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
