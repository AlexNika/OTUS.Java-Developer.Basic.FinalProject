package ru.alexnika.faker.http.server.processors;

import ru.alexnika.faker.http.server.requestanalyzer.HttpRequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;

public class HomePageRequestProcessor implements RequestProcessor {

    @Override
    public void execute(HttpRequestParser request, @NotNull OutputStream out) throws IOException {
        TemplateRequestPreprocessor templateRequest = new TemplateRequestPreprocessor();
        String responseBody = "<html><body><h1>AlexNika's FAKER server answering...</h1><h2>Good bye!</h2></body></html>";
        String response = templateRequest.prepareResponse(200, "OK", "text/html", responseBody);
        out.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
