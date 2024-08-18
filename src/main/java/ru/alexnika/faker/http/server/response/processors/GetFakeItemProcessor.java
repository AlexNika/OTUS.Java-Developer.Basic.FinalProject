package ru.alexnika.faker.http.server.response.processors;

import ru.alexnika.faker.http.server.domain.FakeItem;
import ru.alexnika.faker.http.server.domain.FakeItemsRepository;
import ru.alexnika.faker.http.server.exceptions.BadRequestException;
import ru.alexnika.faker.http.server.request.HttpAccept;
import ru.alexnika.faker.http.server.request.HttpRequest;
import ru.alexnika.faker.http.server.response.HttpResponse;
import ru.alexnika.faker.http.server.response.Response;

import java.io.OutputStream;

import com.google.gson.*;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("FieldMayBeFinal")
public class GetFakeItemProcessor extends Processor {

    public GetFakeItemProcessor(FakeItemsRepository fakeItemsRepository) {
        super(fakeItemsRepository);
    }

    @Override
    public void execute(@NotNull HttpRequest request, OutputStream out) {
        logger.info("GetFakeItem processor executed");
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
        HttpAccept acceptType = request.getAcceptType();
        try {
            Response httpResponse;
            if (fakeItem == null) {
                logger.info("There is no fake item with id={}. Nothing to show.", fakeItemId);
                httpResponse = HttpResponse.noContent(acceptType);
                response = templateRequest.prepareResponseWithoutBody(httpResponse);
            } else {
                responseBody = gson.toJson(fakeItem);
                httpResponse = HttpResponse.ok(acceptType, responseBody);
                response = templateRequest.prepareResponse(httpResponse);
            }
            logger.debug("response: {}", response);
            send(out, response);
        } catch (JsonParseException e) {
            logger.error("Invalid format of incoming JSON object", e);
            throw new BadRequestException("Invalid format of incoming JSON object");
        }
    }
}
