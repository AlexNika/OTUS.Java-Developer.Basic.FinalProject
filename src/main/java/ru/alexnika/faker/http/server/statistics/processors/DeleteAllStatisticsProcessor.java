package ru.alexnika.faker.http.server.statistics.processors;

import org.jetbrains.annotations.NotNull;
import ru.alexnika.faker.http.server.request.HttpAccept;
import ru.alexnika.faker.http.server.request.HttpRequest;
import ru.alexnika.faker.http.server.response.HttpResponse;
import ru.alexnika.faker.http.server.response.Response;
import ru.alexnika.faker.http.server.response.processors.Processor;
import ru.alexnika.faker.http.server.statistics.StatisticsServiceJdbc;

import java.io.OutputStream;

public class DeleteAllStatisticsProcessor extends Processor {

    @Override
    public void execute(@NotNull HttpRequest request, OutputStream out) {
        logger.info("DeleteAllStatistics processor executed");
        int result = -1;
        try {
            result = StatisticsServiceJdbc.deleteAll();
            logger.debug("result of delete all statistics record: {}", result);
        } catch (ClassNotFoundException e) {
            logger.error("Processor tries to delete all statistics record from database", e);
        }
        String response;
        HttpAccept acceptType = request.getAcceptType();
        Response httpresponse;
        if (result >= 0) {
            httpresponse = HttpResponse.ok(acceptType);
        } else {
            httpresponse = HttpResponse.error404(acceptType);
        }
        response = templateRequest.prepareResponse(httpresponse);
        send(out, response);
    }
}
