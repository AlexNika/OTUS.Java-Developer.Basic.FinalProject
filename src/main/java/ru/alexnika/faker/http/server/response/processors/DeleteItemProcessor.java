package ru.alexnika.faker.http.server.response.processors;

import ru.alexnika.faker.http.server.exceptions.BadRequestException;
import ru.alexnika.faker.http.server.domain.FakeItemsRepository;
import ru.alexnika.faker.http.server.request.HttpAccept;
import ru.alexnika.faker.http.server.request.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.jetbrains.annotations.NotNull;
import ru.alexnika.faker.http.server.response.HttpResponse;
import ru.alexnika.faker.http.server.response.Response;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class DeleteItemProcessor extends Processor {
    private FakeItemsRepository fakeItemsRepository;

    public DeleteItemProcessor(FakeItemsRepository fakeItemsRepository) {
        this.fakeItemsRepository = fakeItemsRepository;
    }

    @Override
    public void execute(@NotNull HttpRequest request, OutputStream out) {
        logger.info("DeleteItem processor executed");
        String response;
        if (!request.containsParameter("id")) {
            logger.error("There is no parameter 'id' in URI request. Nothing to delete.");
            throw new BadRequestException("There is no parameter 'id' in URI request. Nothing to delete.");
        }
        long deleteId;
        try {
            deleteId = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            logger.error("The parameter 'id' has incorrect type", e);
            throw new BadRequestException("The parameter 'id' has incorrect type");
        }
        HttpAccept acceptType = request.getAcceptType();
        Response httpresponse;
        if (fakeItemsRepository.delete(deleteId)) {
            httpresponse = HttpResponse.ok(acceptType);
            logger.info("The fake item with id={} has been deleted successfully", deleteId);
        } else {
            httpresponse = HttpResponse.noContent(acceptType);
            logger.info("There is no fake item with id={}. Nothing to delete.", deleteId);
        }
        response = templateRequest.prepareResponseWithoutBody(httpresponse);
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("I/O error occurs", e);
        }
    }
}
