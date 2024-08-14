package ru.alexnika.faker.http.server.processors;

import ru.alexnika.faker.http.server.domain.FakeItem;
import ru.alexnika.faker.http.server.domain.FakeItemsRepository;
import ru.alexnika.faker.http.server.exceptions.BadRequestException;
import ru.alexnika.faker.http.server.requestanalyzer.HttpRequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class UpdateFakeItemProcessor implements RequestProcessor {
    private static final Logger logger = LogManager.getLogger(UpdateFakeItemProcessor.class.getName());
    private FakeItemsRepository fakeItemsRepository;

    public UpdateFakeItemProcessor(FakeItemsRepository fakeItemsRepository) {
        this.fakeItemsRepository = fakeItemsRepository;
    }

    @Override
    public void execute(@NotNull HttpRequestParser request, OutputStream out) {
        TemplateRequestPreprocessor templateRequest = new TemplateRequestPreprocessor();
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
        try {
            if (firstSymbolOfRequestBody.equals('[')) {
                requestedToUpdatesFakeItems = gson.fromJson(requestBody, FakeItemsListType);
                logger.debug("requestedToUpdatesFakeItems: {}", requestedToUpdatesFakeItems.toString());
                responseBody = gson.toJson(requestedToUpdatesFakeItems);
            } else if (firstSymbolOfRequestBody.equals('{')) {
                FakeItem requestedToUpdatesFakeItem = gson.fromJson(requestBody, FakeItem.class);
                requestedToUpdatesFakeItems.add(requestedToUpdatesFakeItem);
                logger.debug("requestedToUpdatesFakeItem: {}", requestedToUpdatesFakeItem.toString());
                responseBody = gson.toJson(requestedToUpdatesFakeItem);
            } else {
                throw new JsonSyntaxException("Invalid format of incoming JSON object.\n" +
                        "JSON string have to start with symbol '{' or '['");
            }
        } catch (JsonSyntaxException e) {
            logger.error("Invalid format of incoming JSON object", e);
            throw new BadRequestException("Invalid format of incoming JSON object");
        }
        response = templateRequest.prepareResponse(200, "OK",
                "application/json", responseBody);
        for (FakeItem requestedFakeItem : requestedToUpdatesFakeItems) {
            Long requestedId = checkRequestedId(requestedFakeItem);
            logger.debug("requestedId: {}", requestedId);
            FakeItem fakeItem = fakeItemsRepository.getFakeItemById(requestedId);
            logger.debug("fakeItem: {}", fakeItem);
            if (fakeItem == null) {
                logger.info("FakeItemsRepository does not contain the requested id={} for the PUT method",
                        requestedId);
                response = templateRequest.prepareResponseWithoutBody(204, "No Content");
                break;
            }
            updateFakeItem(requestedId, requestedFakeItem);
        }
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("I/O error occurs", e);
        }
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
