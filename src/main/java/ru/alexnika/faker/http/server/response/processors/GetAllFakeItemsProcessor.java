package ru.alexnika.faker.http.server.response.processors;

import ru.alexnika.faker.http.server.request.HttpAccept;
import ru.alexnika.faker.http.server.request.HttpRequest;
import ru.alexnika.faker.http.server.exceptions.BadRequestException;
import ru.alexnika.faker.http.server.domain.FakeItem;
import ru.alexnika.faker.http.server.domain.FakeItemsRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.*;

import org.jetbrains.annotations.NotNull;
import ru.alexnika.faker.http.server.response.HttpResponse;
import ru.alexnika.faker.http.server.response.Response;

@SuppressWarnings("FieldMayBeFinal")
public class GetAllFakeItemsProcessor extends Processor {
    private FakeItemsRepository fakeItemsRepository;

    public GetAllFakeItemsProcessor(FakeItemsRepository fakeItemsRepository) {
        this.fakeItemsRepository = fakeItemsRepository;
    }

    @Override
    public void execute(@NotNull HttpRequest request, OutputStream out) {
        logger.info("GetAllFakeItems processor executed");
        int fakeItemsQuantity = fakeItemsRepository.getFakeItemsQuantity();
        int requestedFakeItemsQuantity;
        List<FakeItem> fakeItems = fakeItemsRepository.getFakeItems();
        String responseBody;
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
        HttpAccept acceptType = request.getAcceptType();
        logger.debug("acceptType: {}", acceptType);
        try {
            responseBody = gson.toJson(fakeItems);
            Response httpresponse;
            if (fakeItems == null || fakeItems.isEmpty()) {
                httpresponse = HttpResponse.noContent(acceptType);
            } else {
                httpresponse = HttpResponse.ok(acceptType, responseBody);
            }
            response = templateRequest.prepareResponse(httpresponse);
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
