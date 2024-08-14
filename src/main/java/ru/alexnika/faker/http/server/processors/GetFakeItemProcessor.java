package ru.alexnika.faker.http.server.processors;

import ru.alexnika.faker.http.server.domain.FakeItem;
import ru.alexnika.faker.http.server.domain.FakeItemsRepository;
import ru.alexnika.faker.http.server.exceptions.BadRequestException;
import ru.alexnika.faker.http.server.requestanalyzer.HttpRequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.google.gson.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("FieldMayBeFinal")
public class GetFakeItemProcessor implements RequestProcessor {
    private static final Logger logger = LogManager.getLogger(GetFakeItemProcessor.class.getName());
    private FakeItemsRepository fakeItemsRepository;

    public GetFakeItemProcessor(FakeItemsRepository fakeItemsRepository) {
        this.fakeItemsRepository = fakeItemsRepository;
    }

    @Override
    public void execute(@NotNull HttpRequestParser request, OutputStream out) throws IOException {
        TemplateRequestPreprocessor templateRequest = new TemplateRequestPreprocessor();
        FakeItem fakeItem = null;
        long fakeItemId = -1L;
        String responseBody;
        String response;
        if (request.containsParameter("id")) {
            try {
                fakeItemId = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                logger.error("Parameter 'id' has incorrect type", e);
                throw new BadRequestException("Parameter 'id' has incorrect type");
            }
            fakeItem = fakeItemsRepository.getFakeItemById(fakeItemId);
        }
        Gson gson = new Gson();
        try {
            if (fakeItem == null) {
                logger.info("There is no fake item with id={}. Nothing to show.", fakeItemId);
                response = templateRequest.prepareResponseWithoutBody(204, "No Content");
            } else {
                responseBody = gson.toJson(fakeItem);
                response = templateRequest.prepareResponse(200, "OK",
                        "application/json", responseBody);
            }
            try {
                out.write(response.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                logger.error("I/O error occurs", e);
            }
        } catch (JsonParseException e) {
            logger.error("Invalid format of incoming JSON object", e);
            throw new BadRequestException("Invalid format of incoming JSON object");
        }
    }
}
