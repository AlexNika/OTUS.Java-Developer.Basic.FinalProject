package ru.alexnika.faker.http.server.response.processors;

import ru.alexnika.faker.http.server.domain.FakeItem;
import ru.alexnika.faker.http.server.domain.FakeItemsRepository;
import ru.alexnika.faker.http.server.exceptions.BadRequestException;
import ru.alexnika.faker.http.server.response.HttpResponse;
import ru.alexnika.faker.http.server.response.Response;
import ru.alexnika.faker.http.server.request.HttpAccept;
import ru.alexnika.faker.http.server.request.HttpRequest;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class UpdateFakeItemProcessor extends Processor {

    public UpdateFakeItemProcessor(FakeItemsRepository fakeItemsRepository) {
        super(fakeItemsRepository);
    }

    @Override
    public void execute(@NotNull HttpRequest request, OutputStream out) {
        Type FakeItemsListType = new TypeToken<List<FakeItem>>(){}.getType();
        String requestBody = request.getBody();
        if (requestBody == null) {
            logger.error("Method PUT doesn't have the body");
            throw new BadRequestException("Method PUT doesn't have the body");
        }
        Character firstSymbolOfRequestBody = requestBody.charAt(0);
        List<FakeItem> requestedToUpdatesFakeItems = new ArrayList<>(List.of());
        String response;
        String responseBody;
        Gson gson = new Gson();
        HttpAccept acceptType = request.getAcceptType();
        try {
            if (firstSymbolOfRequestBody.equals('[')) {
                requestedToUpdatesFakeItems = gson.fromJson(requestBody, FakeItemsListType);
                responseBody = gson.toJson(requestedToUpdatesFakeItems);
            } else if (firstSymbolOfRequestBody.equals('{')) {
                FakeItem requestedToUpdatesFakeItem = gson.fromJson(requestBody, FakeItem.class);
                requestedToUpdatesFakeItems.add(requestedToUpdatesFakeItem);
                responseBody = gson.toJson(requestedToUpdatesFakeItem);
            } else {
                throw new JsonSyntaxException("Invalid format of incoming JSON object.\n" +
                        "JSON string have to start with symbol '{' or '['");
            }
        } catch (JsonSyntaxException e) {
            logger.error("Invalid format of incoming JSON object", e);
            throw new BadRequestException("Invalid format of incoming JSON object");
        }
        Response httpresponse = HttpResponse.ok(acceptType, responseBody);
        response = templateRequest.prepareResponse(httpresponse);
        for (FakeItem requestedFakeItem : requestedToUpdatesFakeItems) {
            Long requestedId = checkRequestedId(requestedFakeItem);
            FakeItem fakeItem = fakeItemsRepository.getFakeItemById(requestedId);
            if (fakeItem == null) {
                logger.info("FakeItemsRepository does not contain the requested (id={}) for the PUT method",
                        requestedId);
                httpresponse = HttpResponse.noContent(acceptType);
                response = templateRequest.prepareResponseWithoutBody(httpresponse);
                break;
            }
            updateFakeItem(requestedId, requestedFakeItem);
        }
        send(out, response);
    }

    private @NotNull Long checkRequestedId(@NotNull FakeItem requestedFakeItem) {
        Long requestedId = requestedFakeItem.getId();
        if (requestedId == null) {
            logger.error("The incoming JSON object doesn't have identifier (id)");
            throw new BadRequestException("The incoming JSON object doesn't have identifier (id)");
        }
        return requestedId;
    }

    private void updateFakeItem(Long requestedId, @NotNull FakeItem requestedFakeItem) {
        String requestedFirstName = requestedFakeItem.getFirstName();
        String requestedLastName = requestedFakeItem.getLastName();
        String requestedAddress = requestedFakeItem.getAddress();
        String requestedJob = requestedFakeItem.getJob();
        String requestedHobby = requestedFakeItem.getHobby();
        if (requestedFirstName != null) {
            fakeItemsRepository.getFakeItemById(requestedId).setFirstName(requestedFirstName);
        }
        if (requestedLastName != null) {
            fakeItemsRepository.getFakeItemById(requestedId).setLastName(requestedLastName);
        }
        if (requestedAddress != null) {
            fakeItemsRepository.getFakeItemById(requestedId).setAddress(requestedAddress);
        }
        if (requestedJob != null) {
            fakeItemsRepository.getFakeItemById(requestedId).setJob(requestedJob);
        }
        if (requestedHobby != null) {
            fakeItemsRepository.getFakeItemById(requestedId).setHobby(requestedHobby);
        }
    }
}
