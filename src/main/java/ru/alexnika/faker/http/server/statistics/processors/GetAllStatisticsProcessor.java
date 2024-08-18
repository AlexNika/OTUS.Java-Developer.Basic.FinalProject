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
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;

import org.jetbrains.annotations.NotNull;

public class GetAllStatisticsProcessor extends Processor {

    @Override
    public void execute(@NotNull HttpRequest request, OutputStream out) {
        logger.info("GetAllStatistics processor executed");
        int requestedId;
        List<Statistics> statisticsRecords = new ArrayList<>();
        try {
            if (request.containsParameter("method")) {
                String requestedMethod = request.getParameter("method").toUpperCase();
                statisticsRecords = StatisticsServiceJdbc.selectByMethod(requestedMethod);
            } else if (request.containsParameter("id")) {
                try {
                    requestedId = Integer.parseInt(request.getParameter("id"));
                } catch (NumberFormatException e) {
                    logger.error("Parameter 'id' has incorrect type", e);
                    throw new BadRequestException("Parameter 'id' has incorrect type");
                }
                Statistics statisticsRecordById = StatisticsServiceJdbc.selectById(requestedId);
                if (statisticsRecordById == null) {
                    statisticsRecords = null;
                } else {
                    statisticsRecords.add(statisticsRecordById);
                }
            } else {
                statisticsRecords = StatisticsServiceJdbc.selectAll();
            }
        } catch (ClassNotFoundException e) {
            logger.error("Unexpected exception while processor tries to load in a class through its string name", e);
        }
        String responseBody;
        String response;
        Gson gson = new Gson();
        HttpAccept acceptType = request.getAcceptType();
        logger.debug("acceptType: {}", acceptType);
        try {
            Response httpresponse;
            if (statisticsRecords == null || statisticsRecords.isEmpty()) {
                httpresponse = HttpResponse.ok(acceptType, "[]");
            } else {
                if (statisticsRecords.size() == 1) {
                    responseBody = gson.toJson(statisticsRecords.getFirst());
                } else {
                    responseBody = gson.toJson(statisticsRecords);
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
}
