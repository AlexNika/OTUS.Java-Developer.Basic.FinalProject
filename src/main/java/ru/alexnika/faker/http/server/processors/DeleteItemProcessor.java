package ru.alexnika.faker.http.server.processors;

import ru.alexnika.faker.http.server.exceptions.BadRequestException;
import ru.alexnika.faker.http.server.domain.FakeItemsRepository;
import ru.alexnika.faker.http.server.requestanalyzer.HttpRequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class DeleteItemProcessor implements RequestProcessor {
    private static final Logger logger = LogManager.getLogger(DeleteItemProcessor.class.getName());
    private FakeItemsRepository fakeItemsRepository;

    public DeleteItemProcessor(FakeItemsRepository fakeItemsRepository) {
        this.fakeItemsRepository = fakeItemsRepository;
    }

    @Override
    public void execute(@NotNull HttpRequestParser request, OutputStream out) {
        TemplateRequestPreprocessor templateRequest = new TemplateRequestPreprocessor();
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
        String response = templateRequest.prepareResponseWithoutBody(200, "OK");
        if (fakeItemsRepository.delete(deleteId)) {
            logger.info("The fake item with id={} has been deleted successfully", deleteId);
        } else {
            response = templateRequest.prepareResponseWithoutBody(204, "No Content");
            logger.info("There is no fake item with id={}. Nothing to delete.", deleteId);
        }
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("I/O error occurs", e);
        }
    }
}
