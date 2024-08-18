package ru.alexnika.faker.http.server.statistics.processors;

import ru.alexnika.faker.http.server.exceptions.BadRequestException;
import ru.alexnika.faker.http.server.request.HttpAccept;
import ru.alexnika.faker.http.server.request.HttpRequest;
import ru.alexnika.faker.http.server.response.HttpResponse;
import ru.alexnika.faker.http.server.response.Response;
import ru.alexnika.faker.http.server.response.processors.Processor;
import ru.alexnika.faker.http.server.statistics.Statistics;
import ru.alexnika.faker.http.server.statistics.StatisticsServiceJdbc;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

public class UpdateStatisticsProcessor extends Processor {

    @Override
    public void execute(@NotNull HttpRequest request, OutputStream out) {
        Type StatisticsListType = new TypeToken<List<Statistics>>() {}.getType();
        String requestBody = request.getBody();
        if (requestBody == null) {
            logger.error("Method PUT does not have the body");
            throw new BadRequestException("Method PUT does not have the body");
        }
        Character firstSymbolOfRequestBody = requestBody.charAt(0);
        List<Statistics> requestedSR = new ArrayList<>();
        List<Statistics> updatedSR = new ArrayList<>();
        HttpAccept acceptType = request.getAcceptType();
        Gson gson = new Gson();
        try {
            if (firstSymbolOfRequestBody.equals('[')) {
                requestedSR = gson.fromJson(requestBody, StatisticsListType);
            } else if (firstSymbolOfRequestBody.equals('{')) {
                Statistics statisticsRecord = gson.fromJson(requestBody, Statistics.class);
                requestedSR.add(statisticsRecord);
            } else {
                throw new JsonSyntaxException("Invalid format of incoming JSON object.\n" +
                        "JSON string have to start with symbol '{' or '['");
            }
            logger.debug("requestedSR: {}", requestedSR);
            for (Statistics sr : requestedSR) {
                Integer requestedId = checkRequestedId(sr);
                try {
                    Statistics srFromDb = StatisticsServiceJdbc.selectById(requestedId);
                    if (!(srFromDb == null)) {
                        updateStatisticsRecord(sr, srFromDb);
                        updatedSR.add(srFromDb);
                    } else {
                        logger.error("The incoming JSON object doesn't have record (id={}) in the database", sr.getId());
                    }
                } catch (ClassNotFoundException e) {
                    logger.error("Unexpected exception while processor tries to load in a class through its string name", e);
                }
            }
        } catch (JsonSyntaxException e) {
            logger.error("Invalid format of incoming JSON object", e);
            throw new BadRequestException("Invalid format of incoming JSON object");
        }
        String response;
        String responseBody;
        try {
            Response httpresponse;
            if (updatedSR.isEmpty()) {
                httpresponse = HttpResponse.noContent(acceptType);
            } else {
                if (updatedSR.size() == 1) {
                    responseBody = gson.toJson(updatedSR.getFirst());
                } else {
                    responseBody = gson.toJson(updatedSR);
                }
                httpresponse = HttpResponse.ok(acceptType, responseBody);
            }
            response = templateRequest.prepareResponse(httpresponse);
            send(out, response);
        } catch (JsonParseException e) {
            logger.error("Invalid format of incoming JSON object", e);
            throw new BadRequestException("Invalid format of incoming JSON object");
        }
    }

    private @NotNull Integer checkRequestedId(@NotNull Statistics sr) {
        Integer requestedId = sr.getId();
        if (requestedId == null) {
            logger.error("The incoming JSON object doesn't have identifier (id)");
            throw new BadRequestException("The incoming JSON object doesn't have identifier (id)");
        }
        return requestedId;
    }

    private void updateStatisticsRecord(Statistics requestSR, Statistics updatedSR) {
        if (!(requestSR == null) && !(updatedSR == null)) {
            updatedSR.setBilled(requestSR.getBilled());
            try {
                StatisticsServiceJdbc.update(updatedSR.getId(), updatedSR.getBilled());
            } catch (ClassNotFoundException e) {
                logger.error("Unexpected exception while processor tries to load in a class through its string name", e);
            }
        }
    }
}
