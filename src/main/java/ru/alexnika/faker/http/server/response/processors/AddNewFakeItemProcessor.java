package ru.alexnika.faker.http.server.response.processors;

import ru.alexnika.faker.http.server.domain.FakeItem;
import ru.alexnika.faker.http.server.domain.FakeItemsRepository;
import ru.alexnika.faker.http.server.exceptions.BadRequestException;
import ru.alexnika.faker.http.server.request.HttpAccept;
import ru.alexnika.faker.http.server.request.HttpRequest;
import ru.alexnika.faker.http.server.response.HttpResponse;
import ru.alexnika.faker.http.server.response.Response;

import java.io.OutputStream;
import java.util.List;

import com.google.gson.*;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("FieldMayBeFinal")
public class AddNewFakeItemProcessor extends Processor {

    public AddNewFakeItemProcessor(FakeItemsRepository fakeItemsRepository) {
        super(fakeItemsRepository);
    }

    @Override
    public void execute(@NotNull HttpRequest request, OutputStream out) {
        logger.info("AddNewFakeItem processor executed");
        int requestedFakeItemsQuantity = 1;
        List<FakeItem> fakeItems = new java.util.ArrayList<>(List.of());
        String body = request.getBody();
        if (request.containsParameter("quantity")) {
            try {
                requestedFakeItemsQuantity = Integer.parseInt(request.getParameter("quantity"));
            } catch (NumberFormatException e) {
                logger.error("Parameter 'quantity' has incorrect type", e);
                throw new BadRequestException("Parameter 'quantity' has incorrect type");
            }
        }
        logger.debug("requestedFakeItemsQuantity: {}", requestedFakeItemsQuantity);
        try {
            Gson gson = new Gson();
            if (body == null || body.isEmpty()) {
                for (int i = 0; i < requestedFakeItemsQuantity; i++) {
                    fakeItems.add(fakeItemsRepository.add(fakeItemsRepository.getNewFakeItem()));
                }
            } else {
                fakeItems.add(fakeItemsRepository.add(gson.fromJson(body, FakeItem.class)));
            }
            String responseBody = gson.toJson(fakeItems);
            HttpAccept acceptType = request.getAcceptType();
            Response httpresponse = HttpResponse.create(acceptType, responseBody);
            String response = templateRequest.prepareResponse(httpresponse);
            send(out, response);
        } catch (JsonParseException e) {
            super.logger.error("Invalid format of incoming JSON object", e);
            throw new BadRequestException("Invalid format of incoming JSON object");
        }
    }
}