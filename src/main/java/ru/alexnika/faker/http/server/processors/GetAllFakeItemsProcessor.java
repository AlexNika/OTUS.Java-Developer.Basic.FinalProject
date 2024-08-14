package ru.alexnika.faker.http.server.processors;

import ru.alexnika.faker.http.server.requestanalyzer.HttpRequestParser;
import ru.alexnika.faker.http.server.exceptions.BadRequestException;
import ru.alexnika.faker.http.server.domain.FakeItem;
import ru.alexnika.faker.http.server.domain.FakeItemsRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import com.google.gson.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("FieldMayBeFinal")
public class GetAllFakeItemsProcessor implements RequestProcessor {
    private static final Logger logger = LogManager.getLogger(GetAllFakeItemsProcessor.class.getName());
    private FakeItemsRepository fakeItemsRepository;

    public GetAllFakeItemsProcessor(FakeItemsRepository fakeItemsRepository) {
        this.fakeItemsRepository = fakeItemsRepository;
    }

    @Override
    public void execute(@NotNull HttpRequestParser request, OutputStream out) {
        TemplateRequestPreprocessor templateRequest = new TemplateRequestPreprocessor();
        int fakeItemsQuantity = fakeItemsRepository.getFakeItemsQuantity();
        int requestedFakeItemsQuantity;
        List<FakeItem> fakeItems = fakeItemsRepository.getFakeItems();
        String jsonBody;
        String response;
        if (request.containsParameter("quantity")) {
            try {
                requestedFakeItemsQuantity = Integer.parseInt(request.getParameter("quantity"));
            } catch (NumberFormatException e) {
                logger.error("Parameter 'quantity' has incorrect type", e);
                throw new BadRequestException("Parameter 'quantity' has incorrect type");
            }
            if (requestedFakeItemsQuantity <= fakeItemsQuantity) {
                fakeItems = fakeItemsRepository.getFakeItems(requestedFakeItemsQuantity);
            }
        }
        Gson gson = new Gson();
        try {
            jsonBody = gson.toJson(Objects.requireNonNullElse(fakeItems, "{}"));
            if (fakeItems == null || fakeItems.isEmpty()) {
                response = templateRequest.prepareResponse(204, "No Content",
                        "application/json", jsonBody);
            } else {
                response = templateRequest.prepareResponse(200, "OK",
                        "application/json", jsonBody);
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
